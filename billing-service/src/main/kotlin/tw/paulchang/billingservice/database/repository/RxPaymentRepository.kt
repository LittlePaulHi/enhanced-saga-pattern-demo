package tw.paulchang.billingservice.database.repository

import io.reactivex.rxjava3.core.Maybe
import org.springframework.data.repository.reactive.RxJava3CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.billingservice.database.model.PaymentModel

@Repository
interface RxPaymentRepository : RxJava3CrudRepository<PaymentModel, Long> {
    fun findByCustomerIdAndPaymentTypeAndBalanceGreaterThanEqual(
        customerId: Long,
        paymentType: String,
        balance: Int,
    ): Maybe<PaymentModel>

    fun findByCustomerIdAndPaymentType(
        customerId: Long,
        paymentType: String,
    ): Maybe<PaymentModel>
}
