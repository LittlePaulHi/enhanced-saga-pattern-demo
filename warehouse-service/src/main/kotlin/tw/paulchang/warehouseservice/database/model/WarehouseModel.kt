package tw.paulchang.warehouseservice.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import tw.paulchang.core.entity.warehouse.Warehouse

@Table(value = "warehouse")
data class WarehouseModel(
    @Id
    private val id: Long,

    val productId: Long,
    var amount: Int,
    var isInStock: Boolean
)

fun WarehouseModel.toWarehouse(): Warehouse {
    return Warehouse(
        productId = this.productId,
        amount = this.amount,
        isInStock = this.isInStock
    )
}
