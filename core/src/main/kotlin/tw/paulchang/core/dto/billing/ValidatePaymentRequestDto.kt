package tw.paulchang.core.dto.billing

data class ValidatePaymentRequestDto(
    val customerId: Long,
    val paymentType: String,
    val amount: Int,
)
