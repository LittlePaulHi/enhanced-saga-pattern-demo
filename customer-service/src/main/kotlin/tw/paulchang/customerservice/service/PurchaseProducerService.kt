package tw.paulchang.customerservice.service

import io.reactivex.rxjava3.core.Maybe
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture
import tw.paulchang.core.model.KafkaTopics
import tw.paulchang.core.model.PurchaseTopicModel

@Service
class PurchaseProducerService(
    private val kafkaTemplate: KafkaTemplate<String, PurchaseTopicModel>
) {
    fun sendPurchaseRecordToKafka(
        purchaseTopicModel: PurchaseTopicModel
    ): Maybe<ListenableFuture<SendResult<String, PurchaseTopicModel>>> {
        return Maybe.just(
            kafkaTemplate.send(KafkaTopics.PURCHASE, purchaseTopicModel)
        )
    }
}
