package tw.paulchang.orchestrator.configuration

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class QuotaCacheWebClientConfiguration {
    @Bean
    @Qualifier("warehouse-quota-cache")
    fun warehouseQuotaCacheClient(@Value("\${service.endpoints.warehouse-quota-cache}") endpoint: String): WebClient {
        return WebClient.builder()
            .baseUrl(endpoint)
            .build()
    }

    @Bean
    @Qualifier("billing-quota-cache")
    fun billingQuotaCacheClient(@Value("\${service.endpoints.billing-quota-cache}") endpoint: String): WebClient {
        return WebClient.builder()
            .baseUrl(endpoint)
            .build()
    }
}
