package tw.paulchang.billingservice.database.dao

import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tw.paulchang.billingservice.database.model.BillingModel
import tw.paulchang.billingservice.database.model.toBilling
import tw.paulchang.billingservice.database.repository.RxBillingRepository
import tw.paulchang.core.entity.billing.Billing
import tw.paulchang.core.usecase.repository.BillingRepository
import java.time.Instant

@Component
class BillingDao(
    private val rxBillingRepository: RxBillingRepository
) : BillingRepository {
    @Transactional
    override fun createBilling(
        customerId: Long,
        orderId: Long,
        amount: Int,
        status: String,
        paidAt: Instant
    ): Single<Billing> {
        return rxBillingRepository.save(
            BillingModel(
                id = null,
                orderId = orderId,
                amount = amount,
                status = status.toUpperCase(),
                createdAt = Instant.now(),
                paidAt = paidAt
            )
        )
            .flatMap {
                Single.just(it.toBilling())
            }
    }
}
