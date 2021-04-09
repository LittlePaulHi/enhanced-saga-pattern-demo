package tw.paulchang.orderservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.order.InitOrderRequestDto
import tw.paulchang.core.dto.order.PlaceOrderRequestDto
import tw.paulchang.core.entity.order.Order
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.order.InitOrderUseCase
import tw.paulchang.core.usecase.order.PlaceOrderUseCase

@RestController
@RequestMapping("/order")
class OrderController(
    private val useCaseExecutor: UseCaseExecutor,
    private val initOrderUseCase: InitOrderUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
) {
    @PostMapping("/init")
    fun initOrder(@RequestBody initOrderRequestDto: InitOrderRequestDto): Single<Order> {
        return useCaseExecutor(
            useCase = initOrderUseCase,
            request = initOrderRequestDto
        )
    }

    @PostMapping("/place")
    fun placeOrder(@RequestBody placeOrderRequestDto: PlaceOrderRequestDto): Single<Order> {
        return useCaseExecutor(
            useCase = placeOrderUseCase,
            request = placeOrderRequestDto
        )
    }
}
