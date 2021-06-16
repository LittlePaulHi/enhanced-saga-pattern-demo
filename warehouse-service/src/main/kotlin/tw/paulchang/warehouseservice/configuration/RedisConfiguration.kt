package tw.paulchang.warehouseservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializationContext.RedisSerializationContextBuilder
import org.springframework.data.redis.serializer.StringRedisSerializer
import tw.paulchang.warehouseservice.redis.model.WarehouseCacheModel

@Configuration
class RedisConfiguration {
    @Bean
    fun redisOperations(factory: ReactiveRedisConnectionFactory): ReactiveRedisOperations<String, WarehouseCacheModel> {
        val serializer: Jackson2JsonRedisSerializer<WarehouseCacheModel> =
            Jackson2JsonRedisSerializer(WarehouseCacheModel::class.java)
        val builder: RedisSerializationContextBuilder<String, WarehouseCacheModel> =
            RedisSerializationContext.newSerializationContext(StringRedisSerializer())
        val context: RedisSerializationContext<String, WarehouseCacheModel> = builder.value(serializer).build()

        return ReactiveRedisTemplate(factory, context)
    }
}
