package tw.paulchang.billingservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.billing.AddPaymentRequestDto
import tw.paulchang.core.dto.billing.PaymentPayRequestDto
import tw.paulchang.core.dto.billing.ValidatePaymentRequestDto
import tw.paulchang.core.entity.billing.Payment
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.billing.AddPaymentUseCase
import tw.paulchang.core.usecase.billing.PaymentPayUseCase
import tw.paulchang.core.usecase.billing.RevertPayUseCase
import tw.paulchang.core.usecase.billing.ValidatePaymentUseCase

@RestController
@RequestMapping("/payment")
class PaymentController(
    private val useCaseExecutor: UseCaseExecutor,
    private val addPaymentUseCase: AddPaymentUseCase,
    private val validatePaymentUseCase: ValidatePaymentUseCase,
    private val paymentPayUseCase: PaymentPayUseCase,
    private val revertPayUseCase: RevertPayUseCase,
) {
    @PostMapping("/add")
    fun addPayment(@RequestBody addPaymentRequestDto: AddPaymentRequestDto): Single<Payment> {
        return useCaseExecutor(
            useCase = addPaymentUseCase,
            request = addPaymentRequestDto
        )
    }

    @PostMapping("/validate")
    fun validate(@RequestBody validatePaymentRequestDto: ValidatePaymentRequestDto): Single<Boolean> {
        return useCaseExecutor(
            useCase = validatePaymentUseCase,
            request = validatePaymentRequestDto
        )
    }

    @PostMapping("/pay")
    fun pay(@RequestBody paymentPayRequestDto: PaymentPayRequestDto): Single<Payment> {
        return useCaseExecutor(
            useCase = paymentPayUseCase,
            request = paymentPayRequestDto
        )
    }

    @PostMapping("/revert/pay")
    fun revertPay(@RequestBody paymentPayRequestDto: PaymentPayRequestDto): Single<Payment> {
        return useCaseExecutor(
            useCase = revertPayUseCase,
            request = paymentPayRequestDto
        )
    }
}
