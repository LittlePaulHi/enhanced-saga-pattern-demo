package tw.paulchang.warehouseservice.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tw.paulchang.core.usecase.UseCaseExecutorImp
import tw.paulchang.core.usecase.warehouse.FetchGoodsFromOrderUseCase
import tw.paulchang.core.usecase.warehouse.GetAllProductsByIdsUseCase
import tw.paulchang.core.usecase.warehouse.GetProductByIdUseCase
import tw.paulchang.core.usecase.warehouse.RevertFetchGoodsUseCase
import tw.paulchang.warehouseservice.database.dao.ProductDao
import tw.paulchang.warehouseservice.database.dao.WarehouseDao
import tw.paulchang.warehouseservice.redis.dao.WarehouseCacheDao

@Configuration
class CoreConfiguration {
    @Bean
    fun useCaseExecutor() = UseCaseExecutorImp()

    @Bean
    fun getProductByIdUseCase(
        productDao: ProductDao
    ) = GetProductByIdUseCase(productDao)

    @Bean
    fun getAllProductsByIdUseCase(
        productDao: ProductDao
    ) = GetAllProductsByIdsUseCase(productDao)

    @Bean
    fun fetchGoodsFromOrderUseCase(
        warehouseDao: WarehouseDao
    ) = FetchGoodsFromOrderUseCase(warehouseDao)

    @Bean
    fun fetchGoodsFromOrderCacheUseCase(
        warehouseCacheDao: WarehouseCacheDao
    ) = FetchGoodsFromOrderUseCase(warehouseCacheDao)

    @Bean
    fun revertFetchGoodsUseCase(
        warehouseDao: WarehouseDao
    ) = RevertFetchGoodsUseCase(warehouseDao)

    @Bean
    fun revertFetchGoodsCacheUseCase(
        warehouseCacheDao: WarehouseCacheDao
    ) = RevertFetchGoodsUseCase(warehouseCacheDao)
}
