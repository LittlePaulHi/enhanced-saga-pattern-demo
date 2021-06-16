package tw.paulchang.billingservice.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import tw.paulchang.billingservice.redis.model.PaymentCacheModel

@Configuration
class RedisConfiguration {
    @Value("\${spring.redis.host}")
    lateinit var redisHost: String

    @Value("\${spring.redis.port}")
    lateinit var redisPort: String

    @Value("\${spring.redis.password}")
    lateinit var password: String

    @Bean
    fun redisConnectionFactory(): LettuceConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(redisHost, redisPort.toInt())
        redisStandaloneConfiguration.password = RedisPassword.of(password)
        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

    @Bean
    fun redisOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, PaymentCacheModel> {
        val serializer: Jackson2JsonRedisSerializer<PaymentCacheModel> =
            Jackson2JsonRedisSerializer(PaymentCacheModel::class.java)
        val builder: RedisSerializationContext.RedisSerializationContextBuilder<String, PaymentCacheModel> =
            RedisSerializationContext.newSerializationContext(StringRedisSerializer())
        val context: RedisSerializationContext<String, PaymentCacheModel> = builder.value(serializer).build()

        return ReactiveRedisTemplate(factory, context)
    }
}
