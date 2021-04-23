package tw.paulchang.billingservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.billingservice.redis.dao.PaymentCacheDao
import tw.paulchang.billingservice.redis.model.PaymentCacheModel
import tw.paulchang.core.dto.billing.PaymentPayRequestDto
import tw.paulchang.core.dto.billing.ValidatePaymentRequestDto
import tw.paulchang.core.entity.billing.Payment
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.billing.PaymentPayUseCase
import tw.paulchang.core.usecase.billing.RevertPayUseCase
import tw.paulchang.core.usecase.billing.ValidatePaymentUseCase

@RestController
@RequestMapping("payment/quota-cache")
class PaymentCacheController(
    private val paymentCacheDao: PaymentCacheDao,
    private val useCaseExecutor: UseCaseExecutor,
    private val validatePaymentCacheUseCase: ValidatePaymentUseCase,
    private val paymentPayCacheUseCase: PaymentPayUseCase,
    private val revertPayCacheUseCase: RevertPayUseCase
) {
    @GetMapping("/init")
    fun initPaymentQuotaCache(): Single<MutableIterable<PaymentCacheModel>> {
        return paymentCacheDao.initPaymentCache()
    }

    @PostMapping("/validate")
    fun validate(@RequestBody validatePaymentRequestDto: ValidatePaymentRequestDto): Single<Boolean> {
        return useCaseExecutor(
            useCase = validatePaymentCacheUseCase,
            request = validatePaymentRequestDto
        )
    }

    @PostMapping("/pay")
    fun pay(@RequestBody paymentPayRequestDto: PaymentPayRequestDto): Single<Payment> {
        return useCaseExecutor(
            useCase = paymentPayCacheUseCase,
            request = paymentPayRequestDto
        )
    }

    @PostMapping("/revert/pay")
    fun revertPay(@RequestBody paymentPayRequestDto: PaymentPayRequestDto): Single<Payment> {
        return useCaseExecutor(
            useCase = revertPayCacheUseCase,
            request = paymentPayRequestDto
        )
    }
}
