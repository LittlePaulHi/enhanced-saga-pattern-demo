package tw.paulchang.core.dto.customer

data class AddCartRequestDto(
    val productId: Long,
    val quantity: Int,
)
