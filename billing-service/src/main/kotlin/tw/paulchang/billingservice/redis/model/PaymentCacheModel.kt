package tw.paulchang.billingservice.redis.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import tw.paulchang.core.entity.billing.Payment
import java.time.Instant

@RedisHash("payment")
data class PaymentCacheModel(
    @Indexed val customerId: Long,
    @Indexed val paymentType: String,
    var balance: Int,
    var updatedAt: Instant,
) {
    @get:Id
    var id: String? = null
}

fun PaymentCacheModel.toPayment(): Payment {
    return Payment(
        id = null,
        customerId = this.customerId,
        paymentType = this.paymentType,
        balance = this.balance,
        updatedAt = this.updatedAt
    )
}
