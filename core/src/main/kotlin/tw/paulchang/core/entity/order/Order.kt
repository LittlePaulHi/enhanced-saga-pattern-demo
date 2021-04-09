package tw.paulchang.core.entity.order

import java.time.Instant

data class Order(
    val id: Long?,
    val customerId: Long,
    val shippingId: Long?,
    val cartIds: List<Long>,
    val amount: Int?,
    val orderStatus: String?,
    val createdAt: Instant,
    val shippedAt: Instant?
)
