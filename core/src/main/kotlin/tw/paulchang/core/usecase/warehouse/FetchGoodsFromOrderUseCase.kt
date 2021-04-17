package tw.paulchang.core.usecase.warehouse

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.warehouse.FetchGoodsRequestDto
import tw.paulchang.core.usecase.UseCase

class FetchGoodsFromOrderUseCase(
    private val warehouseRepository: WarehouseRepository
) : UseCase<FetchGoodsRequestDto, Boolean> {
    interface WarehouseRepository {
        fun fetchGoodsByProductIds(productsWithAmount: Map<String, Int>): Single<Boolean>
    }

    override fun execute(request: FetchGoodsRequestDto): Single<Boolean> {
        return warehouseRepository.fetchGoodsByProductIds(request.productsWithAmount)
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
