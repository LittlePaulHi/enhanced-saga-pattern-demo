package tw.paulchang.core.usecase.warehouse

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.usecase.UseCase
import tw.paulchang.core.usecase.exception.NotFoundException

class FetchGoodsFromOrderUseCase(
    private val warehouseRepository: WarehouseRepository
) : UseCase<FetchGoodsFromOrderUseCase.Request, Boolean> {
    interface WarehouseRepository {
        fun fetchGoodsByProductId(productId: Long, amount: Int): Single<Boolean>
    }

    override fun execute(request: Request): Single<Boolean> {
        return warehouseRepository.fetchGoodsByProductId(request.productId, request.amount)
            .onErrorResumeNext {
                return@onErrorResumeNext if (it is NoSuchElementException) {
                    Single.error(NotFoundException("Product with id ${request.productId} is not exist!"))
                } else {
                    Single.error(it)
                }
            }
    }

    data class Request(
        val productId: Long,
        val amount: Int
    )
}
