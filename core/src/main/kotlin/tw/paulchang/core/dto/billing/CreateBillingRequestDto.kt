package tw.paulchang.core.dto.billing

import java.time.Instant

data class CreateBillingRequestDto(
    val customerId: Long,
    val orderId: Long,
    val amount: Int,
    val status: String,
    val paidAt: Instant,
)
