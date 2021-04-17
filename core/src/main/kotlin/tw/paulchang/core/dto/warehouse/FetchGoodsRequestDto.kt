package tw.paulchang.core.dto.warehouse

data class FetchGoodsRequestDto(
    val productsWithAmount: Map<String, Int>,
)
