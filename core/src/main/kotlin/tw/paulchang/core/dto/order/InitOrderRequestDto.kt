package tw.paulchang.core.dto.order

data class InitOrderRequestDto(
    val customerId: Long,
    val cartIds: List<Long>,
)
