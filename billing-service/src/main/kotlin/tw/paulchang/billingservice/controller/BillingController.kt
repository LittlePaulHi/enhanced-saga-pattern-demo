package tw.paulchang.billingservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.billing.CreateBillingRequestDto
import tw.paulchang.core.entity.billing.Billing
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.billing.CreateBillingUseCase

@RestController
@RequestMapping("/billing")
class BillingController(
    private val useCaseExecutor: UseCaseExecutor,
    private val createBillingUseCase: CreateBillingUseCase
) {
    @PostMapping("/create")
    fun createBilling(@RequestBody createBillingRequestDto: CreateBillingRequestDto): Single<Billing> {
        return useCaseExecutor(
            useCase = createBillingUseCase,
            request = createBillingRequestDto
        )
    }
}
