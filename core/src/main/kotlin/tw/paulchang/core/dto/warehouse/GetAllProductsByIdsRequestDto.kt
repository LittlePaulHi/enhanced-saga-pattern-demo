package tw.paulchang.core.dto.warehouse

data class GetAllProductsByIdsRequestDto(
    val productIds: List<Long>,
)
