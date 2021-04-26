package tw.paulchang.warehouseservice.service

import mu.KLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import tw.paulchang.core.model.FinishedBuyEventTopicModel
import tw.paulchang.core.model.KafkaTopics
import tw.paulchang.warehouseservice.redis.dao.WarehouseCacheDao

@Service
class CompensateConsumerService(
    private val warehouseCacheDao: WarehouseCacheDao
) {

    @KafkaListener(topics = [KafkaTopics.COMPENSATE_WAREHOUSE_GOODS], groupId = "warehouse-compensate-service", containerFactory = "concurrentConsumerListener")
    fun consumer(message: FinishedBuyEventTopicModel) {
        logger.info { "[Compensate] Consumed message -> $message" }

        warehouseCacheDao.revert(
            message.productsWithAmount
        ).subscribe()
    }

    companion object : KLogging()
}
