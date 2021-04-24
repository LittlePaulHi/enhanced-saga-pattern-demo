package tw.paulchang.billingservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories

@SpringBootApplication
@EnableR2dbcRepositories
@EnableRedisRepositories
class BillingServiceApplication

fun main(args: Array<String>) {
    runApplication<BillingServiceApplication>(*args)
}
