package tw.paulchang.billingservice.redis.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.data.redis.core.index.Indexed
import tw.paulchang.core.entity.billing.Payment
import java.time.Instant

data class PaymentCacheModel(
    @JsonProperty("id") @Indexed val id: String,
    @JsonProperty("customerId") @Indexed val customerId: Long,
    @JsonProperty("paymentType") @Indexed val paymentType: String,
    @JsonProperty("balance") var balance: Int,

    @JsonProperty("updatedAt")
    @JsonSerialize(using = InstantJsonSerializer::class)
    @JsonDeserialize(using = InstantJsonDeserializer::class)
    var updatedAt: Instant,
)

fun PaymentCacheModel.toPayment(): Payment {
    return Payment(
        id = null,
        customerId = this.customerId,
        paymentType = this.paymentType,
        balance = this.balance,
        updatedAt = this.updatedAt
    )
}
