package tw.paulchang.core.entity.shipping

import java.time.Instant

data class Shipping(
    val id: Long?,
    val orderId: Long,
    val shippingType: String,
    val createdAt: Instant,
)
