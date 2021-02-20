package tw.paulchang.warehouseservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tw.paulchang.core.usecase.UseCaseExecutorImp

@Configuration
class CoreConfiguration {
    @Bean
    fun useCaseExecutor() = UseCaseExecutorImp()
}
