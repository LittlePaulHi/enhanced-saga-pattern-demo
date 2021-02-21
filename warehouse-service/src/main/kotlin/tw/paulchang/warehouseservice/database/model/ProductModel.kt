package tw.paulchang.warehouseservice.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import tw.paulchang.core.entity.warehouse.Product

@Table(value = "product")
data class ProductModel(
    @Id
    val id: Long?,

    val title: String,
    val price: Double,
)

fun ProductModel.toProduct(): Product {
    return Product(
        title = this.title,
        price = this.price
    )
}
