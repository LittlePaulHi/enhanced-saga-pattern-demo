package tw.paulchang.warehouseservice.service

import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Service
import tw.paulchang.warehouseservice.database.dao.WarehouseDao

@Service
class WarehouseService(
    private val warehouseDao: WarehouseDao
) {
    fun fetchGoods(productId: Long, amount: Int): Single<Boolean> {
        // TODO quota-cache
        return warehouseDao.fetchGoodsByProductId(productId, amount)
    }
}
