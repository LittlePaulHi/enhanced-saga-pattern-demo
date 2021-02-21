package tw.paulchang.warehouseservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.warehouse.FetchGoodsFromOrderUseCase

@RestController
@RequestMapping("/warehouse")
class WarehouseController(
    private val useCaseExecutor: UseCaseExecutor,
    private val fetchGoodsFromOrderUseCase: FetchGoodsFromOrderUseCase
) {
    @PostMapping("/fetchGoods")
    fun fetchGoodsFromOrder(@RequestBody request: FetchGoodsFromOrderUseCase.Request): Single<Boolean> {
        return useCaseExecutor(
            useCase = fetchGoodsFromOrderUseCase,
            request = request
        )
    }
}
