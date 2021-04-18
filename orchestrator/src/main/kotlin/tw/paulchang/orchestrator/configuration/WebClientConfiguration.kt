package tw.paulchang.orchestrator.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {
    @Bean
    @Qualifier("order")
    fun orderClient(@Value("\${service.endpoints.order}") endpoint: String): WebClient {
        return WebClient.builder()
            .baseUrl(endpoint)
            .build()
    }

    @Bean
    @Qualifier("warehouse")
    fun warehouseClient(@Value("\${service.endpoints.warehouse}") endpoint: String): WebClient {
        return WebClient.builder()
            .baseUrl(endpoint)
            .build()
    }

    @Bean
    @Qualifier("billing")
    fun billingClient(@Value("\${service.endpoints.billing}") endpoint: String): WebClient {
        return WebClient.builder()
            .baseUrl(endpoint)
            .build()
    }

    @Bean
    @Qualifier("shipping")
    fun shippingClient(@Value("\${service.endpoints.shipping}") endpoint: String): WebClient {
        return WebClient.builder()
            .baseUrl(endpoint)
            .build()
    }
}
