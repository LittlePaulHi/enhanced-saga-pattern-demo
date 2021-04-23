package tw.paulchang.billingservice.redis.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.billingservice.redis.model.PaymentCacheModel

@Repository
interface PaymentCacheRepository : CrudRepository<PaymentCacheModel, Long> {
    fun findByCustomerIdAndPaymentType(
        customerId: Long,
        paymentType: String,
    ): PaymentCacheModel
}
