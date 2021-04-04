package tw.paulchang.customerservice.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import tw.paulchang.core.entity.customer.Cart
import java.time.Instant

@Table(value = "cart")
data class CartModel(
    @Id
    val id: Long?,

    val productId: Long,
    val quantity: Int,
    var createdAt: Instant = Instant.now(),
)

fun CartModel.toCart(): Cart {
    return Cart(
        productId = this.productId,
        quantity = this.quantity,
        createdAt = this.createdAt
    )
}
