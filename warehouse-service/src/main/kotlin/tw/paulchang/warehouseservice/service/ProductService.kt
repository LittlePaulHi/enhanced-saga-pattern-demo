package tw.paulchang.warehouseservice.service

import io.reactivex.rxjava3.core.Maybe
import org.springframework.stereotype.Service
import tw.paulchang.core.entity.warehouse.Product
import tw.paulchang.warehouseservice.database.dao.ProductDao

@Service
class ProductService(
    private val productDao: ProductDao
) {
    fun getProduct(productId: Long): Maybe<Product> {
        // TODO quota-cache
        return productDao.getByProductId(productId)
    }
}
