package tw.paulchang.shippingservice.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import tw.paulchang.core.entity.shipping.Shipping
import java.time.Instant

@Table("shipping")
data class ShippingModel(
    @Id
    val id: Long?,

    val orderId: Long,
    val shippingType: String,
    val createdAt: Instant = Instant.now(),
)

fun ShippingModel.toShipping(): Shipping {
    return Shipping(
        id = this.id,
        orderId = this.orderId,
        shippingType = this.shippingType,
        createdAt = this.createdAt
    )
}
