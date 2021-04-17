package tw.paulchang.warehouseservice.database.dao

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import org.springframework.stereotype.Component
import tw.paulchang.core.entity.warehouse.Product
import tw.paulchang.core.usecase.repository.ProductRepository
import tw.paulchang.warehouseservice.database.model.ProductModel
import tw.paulchang.warehouseservice.database.model.toProduct
import tw.paulchang.warehouseservice.database.repository.RxProductRepository

@Component
class ProductDao(
    private val rxProductRepository: RxProductRepository
) : ProductRepository {
    override fun getByProductId(productId: Long): Maybe<Product> {
        return rxProductRepository.findById(productId)
            .defaultIfEmpty(
                ProductModel(
                    id = -1L,
                    title = "",
                    price = -1.0,
                )
            )
            .flatMapMaybe {
                if (it.price == -1.0) {
                    Maybe.empty()
                } else {
                    Maybe.just(
                        it.toProduct()
                    )
                }
            }
    }

    override fun getAllProductsByIds(productIds: List<Long>): Flowable<Product> {
        return rxProductRepository.findAllById(productIds)
            .map {
                it.toProduct()
            }
    }
}
