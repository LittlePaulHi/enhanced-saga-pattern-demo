package tw.paulchang.warehouseservice.database

import io.r2dbc.spi.ConnectionFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import tw.paulchang.warehouseservice.database.model.ProductModel
import tw.paulchang.warehouseservice.database.model.WarehouseModel
import tw.paulchang.warehouseservice.database.repository.RxProductRepository
import tw.paulchang.warehouseservice.database.repository.RxWarehouseRepository

@DataR2dbcTest
@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@SpringBootApplication(exclude = [RedisAutoConfiguration::class, RedisRepositoriesAutoConfiguration::class])
class RepositoryTest {
    companion object {
        @Container
        var postgreSQLContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                (
                    "r2dbc:postgresql://" + postgreSQLContainer.host + ":" + postgreSQLContainer.firstMappedPort +
                        "/" + postgreSQLContainer.databaseName
                    )
            }
            registry.add("spring.r2dbc.username", postgreSQLContainer::getUsername)
            registry.add("spring.r2dbc.password", postgreSQLContainer::getPassword)
        }
    }

    @TestConfiguration
    internal class TestConfig {
        @Bean
        fun initializer(
            @Qualifier("connectionFactory") connectionFactory: ConnectionFactory
        ): ConnectionFactoryInitializer {
            val initializer = ConnectionFactoryInitializer()
            initializer.setConnectionFactory(connectionFactory)

            val populator = CompositeDatabasePopulator()
            populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("productSchema.sql")))
            populator.addPopulators(ResourceDatabasePopulator(ClassPathResource("warehouseSchema.sql")))
            initializer.setDatabasePopulator(populator)

            return initializer
        }
    }

    @Autowired
    private lateinit var rxProductRepository: RxProductRepository

    @Autowired
    private lateinit var rxWarehouseRepository: RxWarehouseRepository

    @Test
    fun rxProductRepositoryNotNull() {
        assertThat(rxProductRepository).isNotNull
    }

    @Test
    fun rxWarehouseRepositoryNotNull() {
        assertThat(rxWarehouseRepository).isNotNull
    }

    @Test
    fun testProductInsertAndQuery() {
        assertThat(rxProductRepository.findById(1L).blockingGet()).isNull()

        rxProductRepository.save(
            ProductModel(
                id = null,
                title = "test1",
                price = 11.0
            )
        ).blockingGet()

        assertThat(rxProductRepository.findById(1L).blockingGet()).isNotNull
    }

    @Test
    fun testWarehouseInsertAndQuery() {
        assertThat(rxWarehouseRepository.findById(1L).blockingGet()).isNull()

        rxWarehouseRepository.save(
            WarehouseModel(
                id = null,
                productId = 1,
                amount = 1,
                isInStock = false
            )
        ).blockingGet()

        assertThat(rxWarehouseRepository.findById(1L).blockingGet()).isNotNull
    }
}
