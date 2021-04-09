package tw.paulchang.core.usecase.order

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.order.PlaceOrderRequestDto
import tw.paulchang.core.entity.order.Order
import tw.paulchang.core.usecase.UseCase

class PlaceOrderUseCase(
    private val orderRepository: OrderRepository
) : UseCase<PlaceOrderRequestDto, Order> {
    interface OrderRepository {
        fun placeOrder(orderId: Long, shippingId: Long, amount: Int, orderStatus: String): Single<Order>
    }

    override fun execute(request: PlaceOrderRequestDto): Single<Order> {
        return orderRepository.placeOrder(
            orderId = request.orderId,
            shippingId = request.shippingId,
            amount = request.amount,
            orderStatus = request.orderStatus
        )
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
