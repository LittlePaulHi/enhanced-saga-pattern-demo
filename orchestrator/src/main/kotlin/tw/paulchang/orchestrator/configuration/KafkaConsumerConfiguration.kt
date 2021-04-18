package tw.paulchang.orchestrator.configuration

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
import tw.paulchang.core.model.PurchaseTopicModel

@Configuration
class KafkaConsumerConfiguration {
    @Value("\${spring.kafka.consumer.bootstrap-servers}")
    private val bootstrapServers: String? = null

    @Bean
    fun consumerFactory(): ConsumerFactory<String, PurchaseTopicModel> {
        val props: MutableMap<String, Any?> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java

        return DefaultKafkaConsumerFactory(
            props,
            StringDeserializer(),
            JsonDeserializer(PurchaseTopicModel::class.java)
        )
    }

    @Bean
    fun concurrentConsumerListener(): ConcurrentKafkaListenerContainerFactory<String, PurchaseTopicModel> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, PurchaseTopicModel>()
        factory.consumerFactory = consumerFactory()

        return factory
    }
}
