package tw.paulchang.core.dto.order

data class PlaceOrderRequestDto(
    val orderId: Long,
    val shippingId: Long,
    val amount: Int,
    val orderStatus: String
)
