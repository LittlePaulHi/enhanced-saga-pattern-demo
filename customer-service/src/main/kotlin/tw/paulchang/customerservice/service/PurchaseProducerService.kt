package tw.paulchang.customerservice.service

import io.reactivex.rxjava3.core.Maybe
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture
import tw.paulchang.customerservice.configuration.KafkaTopicConfiguration.Companion.PURCHASE_TOPIC
import tw.paulchang.customerservice.dto.PurchaseTopicModel

@Service
class PurchaseProducerService(
    private val kafkaTemplate: KafkaTemplate<String, PurchaseTopicModel>
) {
    fun sendPurchaseRecordToKafka(
        purchaseTopicModel: PurchaseTopicModel
    ): Maybe<ListenableFuture<SendResult<String, PurchaseTopicModel>>> {
        return Maybe.just(
            kafkaTemplate.send(PURCHASE_TOPIC, purchaseTopicModel)
        )
    }
}
