package tw.paulchang.billingservice.redis.dao

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import mu.KLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component
import reactor.adapter.rxjava.RxJava3Adapter
import reactor.core.publisher.Flux
import tw.paulchang.billingservice.database.model.PaymentModel
import tw.paulchang.billingservice.database.repository.RxPaymentRepository
import tw.paulchang.billingservice.redis.model.PaymentCacheModel
import tw.paulchang.billingservice.redis.model.toPayment
import tw.paulchang.core.entity.billing.Payment
import tw.paulchang.core.usecase.billing.PaymentPayUseCase
import tw.paulchang.core.usecase.billing.RevertPayUseCase
import tw.paulchang.core.usecase.billing.ValidatePaymentUseCase
import java.time.Instant
import java.util.UUID
import javax.persistence.LockModeType

@Component
class PaymentCacheDao(
    private val rxPaymentRepository: RxPaymentRepository,
    private val paymentReactiveRedisOperations: ReactiveRedisOperations<String, PaymentCacheModel>,
) : ValidatePaymentUseCase.PaymentRepository,
    PaymentPayUseCase.PaymentRepository,
    RevertPayUseCase.PaymentRepository {

    @EventListener(ApplicationReadyEvent::class)
    fun initPaymentCache(): Completable {
        logger.info { "billing-service-quota-cache: init quota" }
        return rxPaymentRepository.findAll()
            .toList()
            .flatMapCompletable { paymentModelList: MutableList<PaymentModel> ->
                RxJava3Adapter.fluxToFlowable(
                    Flux.fromIterable(paymentModelList)
                        .map { paymentModel: PaymentModel ->
                            PaymentCacheModel(
                                id = UUID.nameUUIDFromBytes(
                                    "${paymentModel.customerId}-${paymentModel.paymentType}"
                                        .toByteArray()
                                ).toString(),
                                customerId = paymentModel.customerId,
                                paymentType = paymentModel.paymentType,
                                balance = paymentModel.balance,
                                updatedAt = paymentModel.updatedAt
                            )
                        }
                        .flatMap { paymentModel: PaymentCacheModel ->
                            paymentReactiveRedisOperations.opsForValue().set(
                                paymentModel.id,
                                paymentModel
                            )
                        }
                )
                    .ignoreElements()
            }
    }

    @Lock(LockModeType.READ)
    override fun validate(
        customerId: Long,
        paymentType: String,
        amount: Int
    ): Single<Boolean> {
        logger.info { "billing-service-quota-cache: validating $paymentType payment with amount $$amount" }
        return RxJava3Adapter.monoToSingle(
            paymentReactiveRedisOperations.opsForValue().get(
                UUID.nameUUIDFromBytes(
                    "$customerId-$paymentType"
                        .toByteArray()
                ).toString()
            )
        )
            .flatMap {
                Single.just(it.balance >= amount)
            }
    }

    @Lock(LockModeType.WRITE)
    override fun pay(
        customerId: Long,
        paymentType: String,
        amount: Int
    ): Single<Payment> {
        logger.info { "billing-service-quota-cache: $paymentType payment paid $$amount" }
        return RxJava3Adapter.monoToSingle(
            paymentReactiveRedisOperations.opsForValue().get(
                UUID.nameUUIDFromBytes(
                    "$customerId-$paymentType"
                        .toByteArray()
                ).toString()
            )
        )
            .flatMap { paymentCacheModel: PaymentCacheModel ->
                paymentCacheModel.balance -= amount
                paymentCacheModel.updatedAt = Instant.now()
                return@flatMap RxJava3Adapter.monoToSingle(
                    paymentReactiveRedisOperations.opsForValue().set(
                        UUID.nameUUIDFromBytes(
                            "$customerId-$paymentType"
                                .toByteArray()
                        ).toString(),
                        paymentCacheModel
                    )
                )
                    .flatMap {
                        Single.just(paymentCacheModel.toPayment())
                    }
            }
    }

    @Lock(LockModeType.WRITE)
    override fun revert(customerId: Long, paymentType: String, amount: Int): Single<Payment> {
        logger.info { "billing-service-quota-cache: compensate $paymentType payment (amount=$$amount) to customer-$customerId" }
        return RxJava3Adapter.monoToSingle(
            paymentReactiveRedisOperations.opsForValue().get(
                UUID.nameUUIDFromBytes(
                    "$customerId-$paymentType"
                        .toByteArray()
                ).toString()
            )
        )
            .flatMap { paymentCacheModel: PaymentCacheModel ->
                paymentCacheModel.balance += amount
                paymentCacheModel.updatedAt = Instant.now()
                return@flatMap RxJava3Adapter.monoToSingle(
                    paymentReactiveRedisOperations.opsForValue().set(
                        UUID.nameUUIDFromBytes(
                            "$customerId-$paymentType"
                                .toByteArray()
                        ).toString(),
                        paymentCacheModel
                    )
                )
                    .flatMap {
                        Single.just(paymentCacheModel.toPayment())
                    }
            }
    }

    companion object : KLogging()
}
