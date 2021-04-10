package tw.paulchang.core.usecase.warehouse

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.warehouse.FetchGoodsRequestDto
import tw.paulchang.core.usecase.UseCase
import tw.paulchang.core.usecase.exception.NotFoundException

class RevertFetchGoodsUseCase(
    private val warehouseRepository: WarehouseRepository
) : UseCase<FetchGoodsRequestDto, Boolean> {
    interface WarehouseRepository {
        fun revert(productId: Long, amount: Int): Single<Boolean>
    }

    override fun execute(request: FetchGoodsRequestDto): Single<Boolean> {
        return warehouseRepository.revert(request.productId, request.amount)
            .onErrorResumeNext {
                return@onErrorResumeNext if (it is NoSuchElementException) {
                    Single.error(NotFoundException("Product with id ${request.productId} is not exist!"))
                } else {
                    Single.error(it)
                }
            }
    }
}
