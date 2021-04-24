package tw.paulchang.warehouseservice.service

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import tw.paulchang.core.model.FinishedBuyEventTopicModel
import tw.paulchang.core.model.KafkaTopics
import tw.paulchang.warehouseservice.database.dao.WarehouseDao

@Service
class EventualCommitConsumerService(
    private val warehouseDao: WarehouseDao
) {

    @KafkaListener(topics = [KafkaTopics.FINISHED_BUY_EVENT], groupId = "warehouse-sync-service", containerFactory = "concurrentConsumerListener")
    fun consumer(message: FinishedBuyEventTopicModel) {
        warehouseDao.fetchGoodsByProductIds(
            message.productsWithAmount
        ).subscribe()
    }
}
