package tw.paulchang.orderservice.database.repository

import org.springframework.data.repository.reactive.RxJava3CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.orderservice.database.model.OrderModel

@Repository
interface RxOrderRepository : RxJava3CrudRepository<OrderModel, Long>
