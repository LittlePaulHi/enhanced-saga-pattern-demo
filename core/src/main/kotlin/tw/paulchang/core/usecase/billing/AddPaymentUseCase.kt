package tw.paulchang.core.usecase.billing

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.billing.AddPaymentRequestDto
import tw.paulchang.core.entity.billing.Payment
import tw.paulchang.core.usecase.UseCase

class AddPaymentUseCase(
    private val paymentRepository: PaymentRepository
) : UseCase<AddPaymentRequestDto, Payment> {
    interface PaymentRepository {
        fun addPayment(
            customerId: Long,
            paymentType: String,
            balance: Int
        ): Single<Payment>
    }

    override fun execute(request: AddPaymentRequestDto): Single<Payment> {
        return paymentRepository.addPayment(
            customerId = request.customerId,
            paymentType = request.paymentType.toUpperCase(),
            balance = request.balance
        )
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
