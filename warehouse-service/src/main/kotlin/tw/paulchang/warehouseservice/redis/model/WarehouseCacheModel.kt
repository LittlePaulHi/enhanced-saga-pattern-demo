package tw.paulchang.warehouseservice.redis.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash("warehouse")
data class WarehouseCacheModel(
    @Indexed val productId: Long,
    var amount: Int,
    var isInStock: Boolean
) {
    @get:Id
    var id: String? = null
}
