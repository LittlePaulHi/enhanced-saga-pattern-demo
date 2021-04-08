package tw.paulchang.billingservice.database.repository

import org.springframework.data.repository.reactive.RxJava3CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.billingservice.database.model.BillingModel

@Repository
interface RxBillingRepository : RxJava3CrudRepository<BillingModel, Long>
