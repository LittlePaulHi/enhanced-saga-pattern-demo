package tw.paulchang.shippingservice.database.repository

import org.springframework.data.repository.reactive.RxJava3CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.shippingservice.database.model.ShippingModel

@Repository
interface RxShippingRepository : RxJava3CrudRepository<ShippingModel, Long>
