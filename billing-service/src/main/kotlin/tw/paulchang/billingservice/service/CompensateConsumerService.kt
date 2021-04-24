package tw.paulchang.billingservice.service

import mu.KLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import tw.paulchang.billingservice.redis.dao.PaymentCacheDao
import tw.paulchang.core.model.FinishedBuyEventTopicModel
import tw.paulchang.core.model.KafkaTopics

@Service
class CompensateConsumerService(
    private val paymentCacheDao: PaymentCacheDao
) {

    @KafkaListener(topics = [KafkaTopics.COMPENSATE_PAYMENT], groupId = "billing-compensate-service", containerFactory = "concurrentConsumerListener")
    fun consumer(message: FinishedBuyEventTopicModel) {
        logger.info { "[Compensate] Consumed message -> $message" }

        paymentCacheDao.revert(
            customerId = message.customerId,
            paymentType = message.paymentType,
            amount = message.amount
        ).subscribe()
    }

    companion object : KLogging()
}
