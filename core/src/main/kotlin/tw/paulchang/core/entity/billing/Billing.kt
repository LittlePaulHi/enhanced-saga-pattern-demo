package tw.paulchang.core.entity.billing

import java.time.Instant

data class Billing(
    val id: Long?,
    val orderId: Long,
    val amount: Int,
    var status: String,
    val createdAt: Instant,
    val paidAt: Instant,
)
