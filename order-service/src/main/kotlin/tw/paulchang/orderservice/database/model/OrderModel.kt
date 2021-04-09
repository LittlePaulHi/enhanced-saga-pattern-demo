package tw.paulchang.orderservice.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import tw.paulchang.core.entity.order.Order
import java.time.Instant

@Table(value = "order_table")
data class OrderModel(
    @Id
    val id: Long?,

    val customerId: Long,
    var shippingId: Long?,
    val cartIds: List<Long>,
    var amount: Int?,
    var orderStatus: String?,
    val createdAt: Instant = Instant.now(),
    var shippedAt: Instant?
)

fun OrderModel.toOrder(): Order {
    return Order(
        id = this.id,
        customerId = this.customerId,
        shippingId = this.shippingId,
        cartIds = this.cartIds,
        amount = this.amount,
        orderStatus = this.orderStatus,
        createdAt = this.createdAt,
        shippedAt = this.shippedAt
    )
}
