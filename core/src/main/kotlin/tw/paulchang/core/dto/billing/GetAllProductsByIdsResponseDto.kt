package tw.paulchang.core.dto.billing

import tw.paulchang.core.entity.warehouse.Product

data class GetAllProductsByIdsResponseDto(
    val products: List<Product>,
)
