package tw.paulchang.billingservice.database.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import tw.paulchang.core.entity.billing.Payment
import java.time.Instant

@Table("payment")
data class PaymentModel(
    @Id
    val id: Long?,

    val customerId: Long,
    val paymentType: String,
    var balance: Int,
    var updatedAt: Instant,
)

fun PaymentModel.isPaymentTypeValid(): Boolean {
    return PaymentType.values().any { it.value == this.paymentType.toUpperCase() }
}

fun PaymentModel.toPayment(): Payment {
    return Payment(
        id = this.id,
        customerId = this.customerId,
        paymentType = this.paymentType,
        balance = this.balance,
        updatedAt = this.updatedAt
    )
}
