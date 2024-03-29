package tw.paulchang.orderservice.database.dao

import io.reactivex.rxjava3.core.Single
import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tw.paulchang.core.entity.order.Order
import tw.paulchang.core.usecase.repository.OrderRepository
import tw.paulchang.orderservice.database.model.OrderModel
import tw.paulchang.orderservice.database.model.toOrder
import tw.paulchang.orderservice.database.repository.RxOrderRepository
import java.time.Instant

@Component
class OrderDao(
    private val rxOrderRepository: RxOrderRepository
) : OrderRepository {
    fun isNotExist(orderId: Long): Single<Boolean> {
        return rxOrderRepository.findById(orderId).isEmpty
    }

    @Transactional
    override fun initOrder(customerId: Long, cartIds: List<Long>): Single<Order> {
        logger.info { "order-service: init order for customer-$customerId" }
        return rxOrderRepository.save(
            OrderModel(
                id = null,
                customerId = customerId,
                shippingId = null,
                cartIds = cartIds,
                amount = null,
                orderStatus = null,
                createdAt = Instant.now(),
                shippedAt = null,
            )
        )
            .flatMap {
                Single.just(it.toOrder())
            }
    }

    @Transactional
    override fun placeOrder(orderId: Long, shippingId: Long, amount: Int, orderStatus: String): Single<Order> {
        logger.info { "order-service: complete order on order-$orderId (shippingId=$shippingId, amount=$$amount, status=$orderStatus)" }
        return rxOrderRepository.findById(orderId)
            .toSingle()
            .flatMap {
                it.shippingId = shippingId
                it.amount = amount
                it.orderStatus = orderStatus
                it.shippedAt = if (orderStatus != "FAILED") Instant.now() else null
                return@flatMap rxOrderRepository.save(it)
            }
            .flatMap {
                Single.just(it.toOrder())
            }
    }

    companion object : KLogging()
}
