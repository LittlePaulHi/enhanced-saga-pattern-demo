package tw.paulchang.shippingservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tw.paulchang.core.usecase.UseCaseExecutorImp
import tw.paulchang.core.usecase.shipping.DispatchShippingUseCase
import tw.paulchang.shippingservice.database.dao.ShippingDao

@Configuration
class CoreConfiguration {
    @Bean
    fun useCaseExecutor() = UseCaseExecutorImp()

    @Bean
    fun dispatchShippingUseCase(
        shippingDao: ShippingDao
    ) = DispatchShippingUseCase(shippingDao)
}
