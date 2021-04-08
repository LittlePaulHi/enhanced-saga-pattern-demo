package tw.paulchang.core.dto.billing

data class AddPaymentRequestDto(
    val customerId: Long,
    val paymentType: String,
    val balance: Int,
)
