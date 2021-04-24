package tw.paulchang.orchestrator.service

import io.reactivex.rxjava3.core.Single
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture
import tw.paulchang.core.model.FinishedBuyEventTopicModel
import tw.paulchang.core.model.KafkaTopics

@Service
class FinishedBuyEventProducerService(
    private val kafkaTemplate: KafkaTemplate<String, FinishedBuyEventTopicModel>
) {
    fun sendFinishedBuyEventRecordToKafka(
        finishedBuyEventTopicModel: FinishedBuyEventTopicModel
    ): Single<ListenableFuture<SendResult<String, FinishedBuyEventTopicModel>>> {
        return Single.just(
            kafkaTemplate.send(KafkaTopics.FINISHED_BUY_EVENT, finishedBuyEventTopicModel)
        )
    }
}
