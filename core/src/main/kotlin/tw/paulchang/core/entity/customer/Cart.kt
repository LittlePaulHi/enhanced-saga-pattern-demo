package tw.paulchang.core.entity.customer

import java.time.Instant

data class Cart(
    val productId: Long,
    val quantity: Int,
    var createdAt: Instant,
)
