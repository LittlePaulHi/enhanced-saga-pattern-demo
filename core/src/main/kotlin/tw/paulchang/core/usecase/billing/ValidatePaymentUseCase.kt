package tw.paulchang.core.usecase.billing

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.billing.ValidatePaymentRequestDto
import tw.paulchang.core.usecase.UseCase

class ValidatePaymentUseCase(
    private val paymentRepository: PaymentRepository
) : UseCase<ValidatePaymentRequestDto, Boolean> {
    interface PaymentRepository {
        fun validate(
            customerId: Long,
            paymentType: String,
            amount: Int
        ): Single<Boolean>
    }

    override fun execute(request: ValidatePaymentRequestDto): Single<Boolean> {
        return paymentRepository.validate(
            customerId = request.customerId,
            paymentType = request.paymentType,
            amount = request.amount
        )
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
