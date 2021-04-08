package tw.paulchang.core.dto.shipping

data class DispatchShippingRequestDto(
    val orderId: Long,
    val shippingType: String,
)
