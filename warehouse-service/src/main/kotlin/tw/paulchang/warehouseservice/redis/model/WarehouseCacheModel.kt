package tw.paulchang.warehouseservice.redis.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.redis.core.index.Indexed

data class WarehouseCacheModel(
    @JsonProperty("id") @Indexed val id: String,
    @JsonProperty("productId") @Indexed val productId: Long,
    @JsonProperty("amount") var amount: Int,
    @JsonProperty("isInStock") var isInStock: Boolean
)
