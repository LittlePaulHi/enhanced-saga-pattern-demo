package tw.paulchang.orchestrator.service

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers.io
import mu.KLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import tw.paulchang.core.dto.billing.CreateBillingRequestDto
import tw.paulchang.core.dto.billing.GetAllProductsByIdsResponseDto
import tw.paulchang.core.dto.billing.PaymentPayRequestDto
import tw.paulchang.core.dto.billing.ValidatePaymentRequestDto
import tw.paulchang.core.dto.order.InitOrderRequestDto
import tw.paulchang.core.dto.order.PlaceOrderRequestDto
import tw.paulchang.core.dto.shipping.DispatchShippingRequestDto
import tw.paulchang.core.dto.warehouse.FetchGoodsRequestDto
import tw.paulchang.core.dto.warehouse.GetAllProductsByIdsRequestDto
import tw.paulchang.core.entity.order.Order
import tw.paulchang.core.entity.shipping.Shipping
import tw.paulchang.core.entity.warehouse.Product
import tw.paulchang.core.model.KafkaTopics
import tw.paulchang.core.model.PurchaseTopicModel
import tw.paulchang.orchestrator.webclient.BillingWebClient
import tw.paulchang.orchestrator.webclient.OrderWebClient
import tw.paulchang.orchestrator.webclient.ShippingWebClient
import tw.paulchang.orchestrator.webclient.WarehouseWebClient
import java.time.Instant
import kotlin.properties.Delegates

@Service
class PurchaseConsumerService(
    private val orderWebClient: OrderWebClient,
    private val warehouseWebClient: WarehouseWebClient,
    private val billingWebClient: BillingWebClient,
    private val shippingWebClient: ShippingWebClient,
) {

    @KafkaListener(
        topics = [KafkaTopics.PURCHASE],
        groupId = "orchestrator-baseline",
        containerFactory = "concurrentConsumerListener"
    )
    fun consumer(message: PurchaseTopicModel) {
        logger.info { "Consumed message -> $message" }

        var orderId by Delegates.notNull<Long>()
        var orderAmount = 0

        // workflow-1. warehouse-service fetch goods
        warehouseWebClient.fetchGoods(
            FetchGoodsRequestDto(
                productsWithAmount = message.purchasedProductsWithAmount.mapKeys { it.key.toString() }
            )
        )
            .observeOn(io())
            // workflow-2. orderWebClient init order
            .flatMap { isFetched: Boolean ->
                return@flatMap orderWebClient.initOrder(
                    InitOrderRequestDto(
                        customerId = message.customerId,
                        cartIds = message.cartIds
                    )
                )
                    .flatMap { order: Order ->
                        order.id?.let { orderId = it }
                        if (!isFetched) placeFailedOrder(orderId).flatMap { Single.just(isFetched) }
                        else Single.just(isFetched)
                    }
            }
            .takeIf { isFetched: Single<Boolean> ->
                isFetched.blockingGet()
            }
            ?.let {
                // workflow-3. billing-service validate & collect pay
                warehouseWebClient.getAllProductsByIds(
                    GetAllProductsByIdsRequestDto(
                        productIds = message.purchasedProductsWithAmount.map { it.key }
                    )
                )
                    .flatMap { response: GetAllProductsByIdsResponseDto ->
                        response.products.forEach { product: Product ->
                            message.purchasedProductsWithAmount[product.id!!]?.let { numberOfProducts: Int ->
                                orderAmount += numberOfProducts.times(product.price).toInt()
                            }
                        }

                        return@flatMap Single.just(orderAmount)
                    }
                    .flatMap { amount: Int ->
                        billingWebClient.validate(
                            ValidatePaymentRequestDto(
                                customerId = message.customerId,
                                paymentType = message.paymentType,
                                amount = amount
                            )
                        )
                            .flatMap { isValidate: Boolean ->
                                if (isValidate) {
                                    billingWebClient.pay(
                                        PaymentPayRequestDto(
                                            customerId = message.customerId,
                                            paymentType = message.paymentType,
                                            amount = amount
                                        )
                                    )
                                        .flatMap {
                                            billingWebClient.createBilling(
                                                CreateBillingRequestDto(
                                                    customerId = message.customerId,
                                                    orderId = orderId,
                                                    amount = amount,
                                                    status = "SUCCESS",
                                                    paidAt = Instant.now()
                                                )
                                            )
                                        }
                                        .flatMap {
                                            Single.just(isValidate)
                                        }
                                } else {
                                    // rollback warehouse fetchGoods & mark order as FAILED
                                    warehouseWebClient.revertFetchGoods(
                                        FetchGoodsRequestDto(
                                            productsWithAmount =
                                            message.purchasedProductsWithAmount.mapKeys { it.key.toString() }
                                        )
                                    )
                                        .flatMap {
                                            placeFailedOrder(orderId).flatMap { Single.just(isValidate) }
                                        }
                                }
                            }
                    }
                    .takeIf { isPaymentValidate: Single<Boolean> ->
                        isPaymentValidate.blockingGet()
                    }
                    ?.let {
                        // workflow-4. shipping-service dispatch the delivery
                        shippingWebClient.dispatch(
                            DispatchShippingRequestDto(
                                orderId = orderId,
                                shippingType = message.shippingType
                            )
                        )
                            // workflow-5. order-service place an order
                            .flatMap { shipping: Shipping ->
                                orderWebClient.placeOrder(
                                    PlaceOrderRequestDto(
                                        orderId = orderId,
                                        shippingId = shipping.id!!,
                                        amount = orderAmount,
                                        orderStatus = "SHIPPED"
                                    )
                                )
                            }
                            .subscribe()
                    }
            }
    }

    private fun placeFailedOrder(orderId: Long): Single<Order> {
        return orderWebClient.placeOrder(
            PlaceOrderRequestDto(
                orderId = orderId,
                shippingId = -1,
                amount = 0,
                orderStatus = "FAILED"
            )
        )
    }

    companion object : KLogging()
}
