package tw.paulchang.core.dto.customer

data class PurchaseRequestDto(
    val customerId: Long,
    val cartIds: List<Long>,
    val paymentType: String,
)
