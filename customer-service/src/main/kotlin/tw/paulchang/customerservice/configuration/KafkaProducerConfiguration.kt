package tw.paulchang.customerservice.configuration

import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaOperations
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.SeekToCurrentErrorHandler
import org.springframework.kafka.support.ProducerListener
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.util.backoff.FixedBackOff
import tw.paulchang.customerservice.dto.PurchaseTopicModel

@Configuration
class KafkaProducerConfiguration {
    private val logger = KotlinLogging.logger {}

    @Value("\${spring.kafka.producer.bootstrap-servers}")
    private val bootstrapServers: String? = null

    @Bean
    fun producerConfigs(): Map<String, Any?> {
        val props: MutableMap<String, Any?> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return props
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, PurchaseTopicModel> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, PurchaseTopicModel> {
        val kafkaTemplate = KafkaTemplate(producerFactory())
        kafkaTemplate.setProducerListener(object : ProducerListener<String, PurchaseTopicModel> {
            override fun onSuccess(
                producerRecord: ProducerRecord<String, PurchaseTopicModel>?,
                recordMetadata: RecordMetadata?
            ) {
                logger.info(
                    "[Sent purchase record] Callback: ${recordMetadata?.topic()}," +
                        "partition=${recordMetadata?.partition()} with offset=${recordMetadata?.offset()}"
                )
            }

            override fun onError(
                producerRecord: ProducerRecord<String, PurchaseTopicModel>?,
                recordMetadata: RecordMetadata?,
                exception: Exception?
            ) {
                logger.error("Producer send purchase record ERROR: ${exception?.message}")
                exception?.printStackTrace()
            }
        })

        return kafkaTemplate
    }

    @Bean
    fun errorHandler(template: KafkaOperations<String, PurchaseTopicModel>): SeekToCurrentErrorHandler {
        return SeekToCurrentErrorHandler(
            DeadLetterPublishingRecoverer(template), FixedBackOff(1000L, 2)
        )
    }
}
