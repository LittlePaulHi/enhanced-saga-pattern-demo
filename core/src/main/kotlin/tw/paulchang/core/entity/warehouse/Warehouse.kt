package tw.paulchang.core.entity.warehouse

data class Warehouse(
    val productId: Long,
    val amount: Int,
    val isInStock: Boolean
)
