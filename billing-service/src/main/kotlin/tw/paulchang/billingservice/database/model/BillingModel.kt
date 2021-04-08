package tw.paulchang.billingservice.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import tw.paulchang.core.entity.billing.Billing
import java.time.Instant

@Table("billing")
data class BillingModel(
    @Id
    val id: Long?,

    val orderId: Long,
    val amount: Int,
    var status: String,
    val createdAt: Instant = Instant.now(),
    val paidAt: Instant,
)

fun BillingModel.toBilling(): Billing {
    return Billing(
        id = id,
        orderId = orderId,
        amount = amount,
        status = status,
        createdAt = createdAt,
        paidAt = paidAt
    )
}
