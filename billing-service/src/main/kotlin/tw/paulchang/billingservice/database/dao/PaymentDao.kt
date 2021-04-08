package tw.paulchang.billingservice.database.dao

import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Component
import tw.paulchang.billingservice.database.model.PaymentModel
import tw.paulchang.billingservice.database.model.toPayment
import tw.paulchang.billingservice.database.repository.RxPaymentRepository
import tw.paulchang.core.entity.billing.Payment
import tw.paulchang.core.usecase.repository.PaymentRepository
import java.time.Instant

@Component
class PaymentDao(
    private val rxPaymentRepository: RxPaymentRepository
) : PaymentRepository {
    override fun addPayment(
        customerId: Long,
        paymentType: String,
        balance: Int
    ): Single<Payment> {
        return rxPaymentRepository.save(
            PaymentModel(
                id = null,
                customerId = customerId,
                paymentType = paymentType.toUpperCase(),
                balance = balance,
                updatedAt = Instant.now()
            )
        )
            .flatMap {
                Single.just(it.toPayment())
            }
    }

    override fun validate(
        customerId: Long,
        paymentType: String,
        amount: Int
    ): Single<Boolean> {
        return rxPaymentRepository
            .findByCustomerIdAndPaymentType(
                customerId = customerId,
                paymentType = paymentType.toUpperCase(),
            )
            .toSingle()
            .flatMap {
                Single.just(it.balance >= amount)
            }
    }

    override fun pay(
        customerId: Long,
        paymentType: String,
        amount: Int
    ): Single<Payment> {
        return rxPaymentRepository
            .findByCustomerIdAndPaymentType(
                customerId = customerId,
                paymentType = paymentType.toUpperCase(),
            )
            .toSingle()
            .flatMap {
                it.balance -= amount
                it.updatedAt = Instant.now()
                return@flatMap rxPaymentRepository.save(it)
            }
            .flatMap {
                Single.just(it.toPayment())
            }
    }
}
