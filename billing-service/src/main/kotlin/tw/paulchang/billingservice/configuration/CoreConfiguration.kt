package tw.paulchang.billingservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import tw.paulchang.billingservice.database.dao.BillingDao
import tw.paulchang.core.usecase.UseCaseExecutorImp
import tw.paulchang.core.usecase.billing.CreateBillingUseCase

@Component
class CoreConfiguration {
    @Bean
    fun useCaseExecutor() = UseCaseExecutorImp()

    @Bean
    fun createBillingUseCase(
        billingDao: BillingDao
    ) = CreateBillingUseCase(billingDao)
}
