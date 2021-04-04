package tw.paulchang.customerservice.database.repository

import org.springframework.data.repository.reactive.RxJava3CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.customerservice.database.model.CartModel

@Repository
interface RxCartRepository : RxJava3CrudRepository<CartModel, Long>
