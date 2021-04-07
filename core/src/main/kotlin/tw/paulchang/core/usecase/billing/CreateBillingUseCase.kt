package tw.paulchang.core.usecase.billing

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.billing.CreateBillingRequestDto
import tw.paulchang.core.entity.billing.Billing
import tw.paulchang.core.usecase.UseCase
import java.time.Instant

class CreateBillingUseCase(
    private val billingRepository: BillingRepository
) : UseCase<CreateBillingRequestDto, Billing> {
    interface BillingRepository {
        fun createBilling(
            customerId: Long,
            orderId: Long,
            amount: Int,
            status: String,
            paidAt: Instant
        ): Single<Billing>
    }

    override fun execute(request: CreateBillingRequestDto): Single<Billing> {
        return billingRepository.createBilling(
            customerId = request.customerId,
            orderId = request.orderId,
            amount = request.amount,
            status = request.status,
            paidAt = request.paidAt
        )
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
