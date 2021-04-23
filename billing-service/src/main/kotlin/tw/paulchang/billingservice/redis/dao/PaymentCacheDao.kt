package tw.paulchang.billingservice.redis.dao

import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Component
import tw.paulchang.billingservice.database.model.PaymentModel
import tw.paulchang.billingservice.database.repository.RxPaymentRepository
import tw.paulchang.billingservice.redis.model.PaymentCacheModel
import tw.paulchang.billingservice.redis.model.toPayment
import tw.paulchang.billingservice.redis.repository.PaymentCacheRepository
import tw.paulchang.core.entity.billing.Payment
import tw.paulchang.core.usecase.billing.PaymentPayUseCase
import tw.paulchang.core.usecase.billing.RevertPayUseCase
import tw.paulchang.core.usecase.billing.ValidatePaymentUseCase
import java.time.Instant

@Component
class PaymentCacheDao(
    private val rxPaymentRepository: RxPaymentRepository,
    private val paymentCacheRepository: PaymentCacheRepository
) : ValidatePaymentUseCase.PaymentRepository,
    PaymentPayUseCase.PaymentRepository,
    RevertPayUseCase.PaymentRepository {
    fun initPaymentCache(): Single<MutableIterable<PaymentCacheModel>> {
        return rxPaymentRepository.findAll()
            .toList()
            .flatMap { paymentModelList: MutableList<PaymentModel> ->
                Single.just(
                    paymentCacheRepository.saveAll(
                        paymentModelList.map { paymentModel: PaymentModel ->
                            PaymentCacheModel(
                                customerId = paymentModel.customerId,
                                paymentType = paymentModel.paymentType,
                                balance = paymentModel.balance,
                                updatedAt = paymentModel.updatedAt
                            )
                        }
                    )
                )
            }
    }

    override fun validate(
        customerId: Long,
        paymentType: String,
        amount: Int
    ): Single<Boolean> {
        return Single.just(
            paymentCacheRepository
                .findByCustomerIdAndPaymentType(
                    customerId = customerId,
                    paymentType = paymentType.toUpperCase(),
                )
        )
            .flatMap {
                Single.just(it.balance >= amount)
            }
    }

    override fun pay(
        customerId: Long,
        paymentType: String,
        amount: Int
    ): Single<Payment> {
        return Single.just(
            paymentCacheRepository
                .findByCustomerIdAndPaymentType(
                    customerId = customerId,
                    paymentType = paymentType.toUpperCase(),
                )
        )
            .flatMap {
                it.balance -= amount
                it.updatedAt = Instant.now()
                return@flatMap Single.just(
                    paymentCacheRepository.save(it).toPayment()
                )
            }
    }

    override fun revert(customerId: Long, paymentType: String, amount: Int): Single<Payment> {
        return Single.just(
            paymentCacheRepository
                .findByCustomerIdAndPaymentType(
                    customerId = customerId,
                    paymentType = paymentType.toUpperCase(),
                )
        )
            .flatMap {
                it.balance += amount
                it.updatedAt = Instant.now()
                return@flatMap Single.just(
                    paymentCacheRepository.save(it).toPayment()
                )
            }
    }
}
