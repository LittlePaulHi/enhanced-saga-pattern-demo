package tw.paulchang.core.usecase.billing

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.billing.PaymentPayRequestDto
import tw.paulchang.core.entity.billing.Payment
import tw.paulchang.core.usecase.UseCase

class PaymentPayUseCase(
    private val paymentRepository: PaymentRepository
) : UseCase<PaymentPayRequestDto, Payment> {
    interface PaymentRepository {
        fun pay(
            customerId: Long,
            paymentType: String,
            amount: Int
        ): Single<Payment>
    }

    override fun execute(request: PaymentPayRequestDto): Single<Payment> {
        return paymentRepository.pay(
            customerId = request.customerId,
            paymentType = request.paymentType,
            amount = request.amount
        )
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
