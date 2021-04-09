package tw.paulchang.core.usecase.order

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.order.InitOrderRequestDto
import tw.paulchang.core.entity.order.Order
import tw.paulchang.core.usecase.UseCase

class InitOrderUseCase(
    private val orderRepository: OrderRepository
) : UseCase<InitOrderRequestDto, Order> {
    interface OrderRepository {
        fun initOrder(customerId: Long, cartIds: List<Long>): Single<Order>
    }

    override fun execute(request: InitOrderRequestDto): Single<Order> {
        return orderRepository.initOrder(
            customerId = request.customerId,
            cartIds = request.cartIds.map { it }
        )
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
