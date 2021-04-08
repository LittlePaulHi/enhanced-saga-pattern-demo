package tw.paulchang.core.entity.billing

import java.time.Instant

data class Payment(
    val id: Long?,
    val customerId: Long,
    val paymentType: String,
    var balance: Int,
    var updatedAt: Instant,
)
