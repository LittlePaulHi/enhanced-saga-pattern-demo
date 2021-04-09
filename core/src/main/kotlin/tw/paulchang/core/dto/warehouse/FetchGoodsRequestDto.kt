package tw.paulchang.core.dto.warehouse

data class FetchGoodsRequestDto(
    val productId: Long,
    val amount: Int
)
