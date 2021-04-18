package tw.paulchang.core.usecase.warehouse

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.warehouse.FetchGoodsRequestDto
import tw.paulchang.core.usecase.UseCase
import tw.paulchang.core.usecase.exception.NotFoundException

class RevertFetchGoodsUseCase(
    private val warehouseRepository: WarehouseRepository
) : UseCase<FetchGoodsRequestDto, Boolean> {
    interface WarehouseRepository {
        fun revert(productsWithAmount: Map<String, Int>): Single<Boolean>
    }

    override fun execute(request: FetchGoodsRequestDto): Single<Boolean> {
        return warehouseRepository.revert(request.productsWithAmount)
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
