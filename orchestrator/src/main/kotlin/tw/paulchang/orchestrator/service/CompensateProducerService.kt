package tw.paulchang.orchestrator.service

import io.reactivex.rxjava3.core.Single
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFuture
import tw.paulchang.core.model.FinishedBuyEventTopicModel

@Service
class CompensateProducerService(
    private val kafkaTemplate: KafkaTemplate<String, FinishedBuyEventTopicModel>
) {
    fun sendCompensateRecordToKafka(
        kafkaTopic: String,
        finishedBuyEventTopicModel: FinishedBuyEventTopicModel
    ): Single<ListenableFuture<SendResult<String, FinishedBuyEventTopicModel>>> {
        return Single.just(
            kafkaTemplate.send(kafkaTopic, finishedBuyEventTopicModel)
        )
    }
}
