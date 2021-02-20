package tw.paulchang.warehouseservice.database.repository

import org.springframework.data.repository.reactive.RxJava3CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.warehouseservice.database.model.ProductModel

@Repository
interface RxProductRepository : RxJava3CrudRepository<ProductModel, Long>
