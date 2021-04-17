package tw.paulchang.warehouseservice.database.repository

import io.reactivex.rxjava3.core.Flowable
import org.springframework.data.repository.reactive.RxJava3CrudRepository
import org.springframework.stereotype.Repository
import tw.paulchang.warehouseservice.database.model.WarehouseModel

@Repository
interface RxWarehouseRepository : RxJava3CrudRepository<WarehouseModel, Long> {
    fun findAllByProductIdIn(productIds: Collection<Long>): Flowable<WarehouseModel>
}
