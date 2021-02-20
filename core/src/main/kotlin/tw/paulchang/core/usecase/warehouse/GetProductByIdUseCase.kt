package tw.paulchang.core.usecase.warehouse

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.entity.warehouse.Product
import tw.paulchang.core.usecase.UseCase
import tw.paulchang.core.usecase.exception.NotFoundException

class GetProductByIdUseCase(
    private val productRepository: ProductRepository
) : UseCase<Long, Product> {
    interface ProductRepository {
        fun getByProductId(productId: Long): Maybe<Product>
    }

    override fun execute(request: Long): Single<Product> {
        return productRepository.getByProductId(request)
            .toSingle()
            .onErrorResumeNext {
                return@onErrorResumeNext if (it is NoSuchElementException) {
                    Single.error(NotFoundException("Product with id $request is not exist!"))
                } else {
                    Single.error(it)
                }
            }
    }
}
