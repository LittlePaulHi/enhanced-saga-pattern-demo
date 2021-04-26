package tw.paulchang.orchestrator.service

import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
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
import tw.paulchang.core.model.FinishedBuyEventTopicModel
import tw.paulchang.core.model.KafkaTopics
import tw.paulchang.core.model.PurchaseTopicModel
import tw.paulchang.orchestrator.webclient.OrderWebClient
import tw.paulchang.orchestrator.webclient.ShippingWebClient
import tw.paulchang.orchestrator.webclient.quotacache.BillingCacheWebClient
import tw.paulchang.orchestrator.webclient.quotacache.WarehouseCacheWebClient
import java.time.Instant
import kotlin.properties.Delegates

@Service
class PurchaseQuotaCacheConsumerService(
    private val orderWebClient: OrderWebClient,
    private val warehouseCacheWebClient: WarehouseCacheWebClient,
    private val billingCacheWebClient: BillingCacheWebClient,
    private val shippingWebClient: ShippingWebClient,
    private val compensateProducerService: CompensateProducerService,
    private val finishedBuyEventProducerService: FinishedBuyEventProducerService,
) {
    @KafkaListener(
        topics = [KafkaTopics.PURCHASE_VIA_QUOTA_CACHE],
        groupId = "orchestrator-quota-cache",
        containerFactory = "concurrentConsumerListener"
    )
    fun consumer(message: PurchaseTopicModel) {
        logger.info { "[Quota-Cache] Consumed message -> $message" }

        var orderId by Delegates.notNull<Long>()
        var orderAmount = 0

        // workflow-1. warehouse-service fetch goods
        warehouseCacheWebClient.fetchGoods(
            FetchGoodsRequestDto(
                productsWithAmount = message.purchasedProductsWithAmount.mapKeys { it.key.toString() }
            )
        )
            .observeOn(Schedulers.io())
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
                warehouseCacheWebClient.getAllProductsByIds(
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

                        return@flatMap validatePaymentAndPay(
                            message, orderId, orderAmount
                        )
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
                            // workflow-5. order-service complete an order and produce finish event to Kafka
                            .flatMap { shipping: Shipping ->
                                Single.mergeDelayError(
                                    orderWebClient.placeOrder(
                                        PlaceOrderRequestDto(
                                            orderId = orderId,
                                            shippingId = shipping.id!!,
                                            amount = orderAmount,
                                            orderStatus = "SHIPPED"
                                        )
                                    ),
                                    Single.just(
                                        finishedBuyEventProducerService.sendFinishedBuyEventRecordToKafka(
                                            FinishedBuyEventTopicModel(
                                                productsWithAmount =
                                                message.purchasedProductsWithAmount.mapKeys { it.key.toString() },
                                                customerId = message.customerId,
                                                paymentType = message.paymentType,
                                                amount = orderAmount
                                            )
                                        )
                                    )
                                )
                                    .lastOrError()
                            }
                            .doOnError {
                                // compensate warehouse' fetchGoods & Payment pay if occurs error in the last step
                                Single.just(
                                    FinishedBuyEventTopicModel(
                                        productsWithAmount =
                                        message.purchasedProductsWithAmount.mapKeys { it.key.toString() },
                                        customerId = message.customerId,
                                        paymentType = message.paymentType,
                                        amount = orderAmount
                                    )
                                )
                                    .flatMap { finishedBuyEventTopicModel: FinishedBuyEventTopicModel ->
                                        Single.mergeDelayError(
                                            placeFailedOrder(orderId),
                                            Single.just(
                                                compensateProducerService.sendCompensateRecordToKafka(
                                                    KafkaTopics.COMPENSATE_WAREHOUSE_GOODS,
                                                    finishedBuyEventTopicModel
                                                )
                                            ),
                                            Single.just(
                                                compensateProducerService.sendCompensateRecordToKafka(
                                                    KafkaTopics.COMPENSATE_PAYMENT,
                                                    finishedBuyEventTopicModel
                                                )
                                            )
                                        )
                                            .lastOrError()
                                    }
                                    .subscribe()
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

    private fun validatePaymentAndPay(
        message: PurchaseTopicModel,
        orderId: Long,
        orderAmount: Int,
    ): Single<Boolean> {
        return billingCacheWebClient.validate(
            ValidatePaymentRequestDto(
                customerId = message.customerId,
                paymentType = message.paymentType,
                amount = orderAmount
            )
        )
            .flatMap { isValidate: Boolean ->
                if (isValidate) {
                    billingCacheWebClient.pay(
                        PaymentPayRequestDto(
                            customerId = message.customerId,
                            paymentType = message.paymentType,
                            amount = orderAmount
                        )
                    )
                        .flatMap {
                            billingCacheWebClient.createBilling(
                                CreateBillingRequestDto(
                                    customerId = message.customerId,
                                    orderId = orderId,
                                    amount = orderAmount,
                                    status = "SUCCESS",
                                    paidAt = Instant.now()
                                )
                            )
                        }
                        .flatMap {
                            Single.just(isValidate)
                        }
                } else {
                    // produce event to compensate warehouse fetchGoods & mark order as FAILED
                    Single.mergeDelayError(
                        placeFailedOrder(orderId),
                        Single.just(
                            compensateProducerService.sendCompensateRecordToKafka(
                                KafkaTopics.COMPENSATE_WAREHOUSE_GOODS,
                                FinishedBuyEventTopicModel(
                                    productsWithAmount =
                                    message.purchasedProductsWithAmount.mapKeys { it.key.toString() },
                                    customerId = message.customerId,
                                    paymentType = message.paymentType,
                                    amount = orderAmount
                                )
                            )
                        )
                    )
                        .lastOrError()
                        .flatMap {
                            Single.just(isValidate)
                        }
                }
            }
    }

    companion object : KLogging()
}
