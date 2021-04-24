package tw.paulchang.warehouseservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.kafka.annotation.EnableKafka

@SpringBootApplication
@EnableR2dbcRepositories
@EnableRedisRepositories
@EnableKafka
class WarehouseServiceApplication

fun main(args: Array<String>) {
    runApplication<WarehouseServiceApplication>(*args)
}
