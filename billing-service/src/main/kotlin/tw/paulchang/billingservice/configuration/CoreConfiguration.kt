package tw.paulchang.billingservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import tw.paulchang.billingservice.database.dao.BillingDao
import tw.paulchang.billingservice.database.dao.PaymentDao
import tw.paulchang.billingservice.redis.dao.PaymentCacheDao
import tw.paulchang.core.usecase.UseCaseExecutorImp
import tw.paulchang.core.usecase.billing.AddPaymentUseCase
import tw.paulchang.core.usecase.billing.CreateBillingUseCase
import tw.paulchang.core.usecase.billing.PaymentPayUseCase
import tw.paulchang.core.usecase.billing.RevertPayUseCase
import tw.paulchang.core.usecase.billing.ValidatePaymentUseCase

@Component
class CoreConfiguration {
    @Bean
    fun useCaseExecutor() = UseCaseExecutorImp()

    @Bean
    fun createBillingUseCase(
        billingDao: BillingDao
    ) = CreateBillingUseCase(billingDao)

    @Bean
    fun addPaymentUseCase(
        paymentDao: PaymentDao
    ) = AddPaymentUseCase(paymentDao)

    @Bean
    fun validatePaymentUseCase(
        paymentDao: PaymentDao
    ) = ValidatePaymentUseCase(paymentDao)

    @Bean
    fun validatePaymentCacheUseCase(
        paymentCacheDao: PaymentCacheDao
    ) = ValidatePaymentUseCase(paymentCacheDao)

    @Bean
    fun paymentPayUseCase(
        paymentDao: PaymentDao
    ) = PaymentPayUseCase(paymentDao)

    @Bean
    fun paymentPayCacheUseCase(
        paymentCacheDao: PaymentCacheDao
    ) = PaymentPayUseCase(paymentCacheDao)

    @Bean
    fun revertPayUseCase(
        paymentDao: PaymentDao
    ) = RevertPayUseCase(paymentDao)

    @Bean
    fun revertPayCacheUseCase(
        paymentCacheDao: PaymentCacheDao
    ) = RevertPayUseCase(paymentCacheDao)
}
