package tw.paulchang.core.dto.billing

data class PaymentPayRequestDto(
    val customerId: Long,
    val paymentType: String,
    val amount: Int,
)
