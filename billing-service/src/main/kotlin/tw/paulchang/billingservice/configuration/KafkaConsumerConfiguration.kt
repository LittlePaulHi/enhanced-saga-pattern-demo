package tw.paulchang.billingservice.configuration

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import tw.paulchang.core.model.FinishedBuyEventTopicModel

@Configuration
class KafkaConsumerConfiguration {
    @Value("\${spring.kafka.consumer.bootstrap-servers}")
    private val bootstrapServers: String? = null

    @Bean
    fun consumerFactory(): ConsumerFactory<String, FinishedBuyEventTopicModel> {
        val props: MutableMap<String, Any?> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        return DefaultKafkaConsumerFactory(
            props,
            StringDeserializer(),
            JsonDeserializer(FinishedBuyEventTopicModel::class.java)
        )
    }

    @Bean
    fun concurrentConsumerListener(): ConcurrentKafkaListenerContainerFactory<String, FinishedBuyEventTopicModel> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, FinishedBuyEventTopicModel>()
        factory.consumerFactory = consumerFactory()

        return factory
    }
}
