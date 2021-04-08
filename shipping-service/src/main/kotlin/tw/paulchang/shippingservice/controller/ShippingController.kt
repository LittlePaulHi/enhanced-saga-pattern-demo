package tw.paulchang.shippingservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.shipping.DispatchShippingRequestDto
import tw.paulchang.core.entity.shipping.Shipping
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.shipping.DispatchShippingUseCase

@RestController
@RequestMapping("/shipping")
class ShippingController(
    private val useCaseExecutor: UseCaseExecutor,
    private val dispatchShippingUseCase: DispatchShippingUseCase
) {
    @PostMapping("/dispatch")
    fun dispatch(@RequestBody dispatchShippingRequestDto: DispatchShippingRequestDto): Single<Shipping> {
        return useCaseExecutor(
            useCase = dispatchShippingUseCase,
            request = dispatchShippingRequestDto
        )
    }
}
