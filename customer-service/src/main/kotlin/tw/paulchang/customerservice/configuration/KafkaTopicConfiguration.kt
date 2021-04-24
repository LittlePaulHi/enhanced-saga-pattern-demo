package tw.paulchang.customerservice.configuration

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder
import org.springframework.kafka.core.KafkaAdmin
import tw.paulchang.core.model.KafkaTopics

@Configuration
class KafkaTopicConfiguration {

    @Value("\${spring.kafka.producer.bootstrap-servers}")
    private val bootstrapServers: String? = null

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any?> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        return KafkaAdmin(configs)
    }

    @Bean
    fun newTopic(): NewTopic {
        return TopicBuilder.name(KafkaTopics.PURCHASE).partitions(5).build()
    }
}
