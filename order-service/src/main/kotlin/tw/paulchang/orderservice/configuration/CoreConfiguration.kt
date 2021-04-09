package tw.paulchang.orderservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tw.paulchang.core.usecase.UseCaseExecutorImp
import tw.paulchang.core.usecase.order.InitOrderUseCase
import tw.paulchang.core.usecase.order.PlaceOrderUseCase
import tw.paulchang.orderservice.database.dao.OrderDao

@Configuration
class CoreConfiguration {
    @Bean
    fun useCaseExecutor() = UseCaseExecutorImp()

    @Bean
    fun initOrderUseCase(
        orderDao: OrderDao
    ) = InitOrderUseCase(orderDao)

    @Bean
    fun placeOrderUseCase(
        orderDao: OrderDao
    ) = PlaceOrderUseCase(orderDao)
}
