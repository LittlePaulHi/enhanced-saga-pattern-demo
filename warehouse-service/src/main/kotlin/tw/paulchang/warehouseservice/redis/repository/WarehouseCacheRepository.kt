package tw.paulchang.warehouseservice.redis.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.warehouseservice.redis.model.WarehouseCacheModel

@Repository
interface WarehouseCacheRepository : CrudRepository<WarehouseCacheModel, String> {
    fun findByProductId(productId: Long): WarehouseCacheModel
}
