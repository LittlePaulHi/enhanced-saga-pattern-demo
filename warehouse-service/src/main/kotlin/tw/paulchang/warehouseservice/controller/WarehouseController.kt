package tw.paulchang.warehouseservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.warehouse.FetchGoodsRequestDto
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.warehouse.FetchGoodsFromOrderUseCase
import tw.paulchang.core.usecase.warehouse.RevertFetchGoodsUseCase

@RestController
@RequestMapping("/warehouse")
class WarehouseController(
    private val useCaseExecutor: UseCaseExecutor,
    private val fetchGoodsFromOrderUseCase: FetchGoodsFromOrderUseCase,
    private val revertFetchGoodsUseCase: RevertFetchGoodsUseCase,
) {
    @PostMapping("/fetchGoods")
    fun fetchGoodsFromOrder(@RequestBody request: FetchGoodsRequestDto): Single<Boolean> {
        return useCaseExecutor(
            useCase = fetchGoodsFromOrderUseCase,
            request = request
        )
    }

    @PostMapping("/revert/fetchGoods")
    fun revertFetchGoodsFromOrder(@RequestBody request: FetchGoodsRequestDto): Single<Boolean> {
        return useCaseExecutor(
            useCase = revertFetchGoodsUseCase,
            request = request
        )
    }
}
