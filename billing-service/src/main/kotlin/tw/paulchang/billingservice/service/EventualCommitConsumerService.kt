package tw.paulchang.billingservice.service

import mu.KLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import tw.paulchang.billingservice.database.dao.PaymentDao
import tw.paulchang.core.model.FinishedBuyEventTopicModel
import tw.paulchang.core.model.KafkaTopics

@Service
class EventualCommitConsumerService(
    private val paymentDao: PaymentDao
) {

    @KafkaListener(topics = [KafkaTopics.FINISHED_BUY_EVENT], groupId = "billing-sync-service", containerFactory = "concurrentConsumerListener")
    fun consumer(message: FinishedBuyEventTopicModel) {
        logger.info { "Consumed message -> $message" }

        paymentDao.pay(
            customerId = message.customerId,
            paymentType = message.paymentType,
            amount = message.amount
        ).subscribe()
    }

    companion object : KLogging()
}
