package tw.paulchang.warehouseservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.warehouse.FetchGoodsRequestDto
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.warehouse.FetchGoodsFromOrderUseCase
import tw.paulchang.core.usecase.warehouse.RevertFetchGoodsUseCase
import tw.paulchang.warehouseservice.redis.dao.WarehouseCacheDao
import tw.paulchang.warehouseservice.redis.model.WarehouseCacheModel

@RestController
@RequestMapping("/warehouse/quota-cache")
class WarehouseCacheController(
    private val warehouseCacheDao: WarehouseCacheDao,
    private val useCaseExecutor: UseCaseExecutor,
    private val fetchGoodsFromOrderCacheUseCase: FetchGoodsFromOrderUseCase,
    private val revertFetchGoodsCacheUseCase: RevertFetchGoodsUseCase,
) {
    @GetMapping("/init")
    fun initWarehouseQuotaCache(): Single<MutableIterable<WarehouseCacheModel>> {
        return warehouseCacheDao.initWarehouseCache()
    }

    @PostMapping("/fetchGoods")
    fun fetchGoodsFromOrder(@RequestBody request: FetchGoodsRequestDto): Single<Boolean> {
        return useCaseExecutor(
            useCase = fetchGoodsFromOrderCacheUseCase,
            request = request
        )
    }

    @PostMapping("/revert/fetchGoods")
    fun revertFetchGoodsFromOrder(@RequestBody request: FetchGoodsRequestDto): Single<Boolean> {
        return useCaseExecutor(
            useCase = revertFetchGoodsCacheUseCase,
            request = request
        )
    }
}
