package tw.paulchang.warehouseservice.redis.dao

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Component
import tw.paulchang.core.usecase.warehouse.FetchGoodsFromOrderUseCase
import tw.paulchang.core.usecase.warehouse.RevertFetchGoodsUseCase
import tw.paulchang.warehouseservice.database.model.WarehouseModel
import tw.paulchang.warehouseservice.database.repository.RxWarehouseRepository
import tw.paulchang.warehouseservice.redis.model.WarehouseCacheModel
import tw.paulchang.warehouseservice.redis.repository.WarehouseCacheRepository

@Component
class WarehouseCacheDao(
    private val rxWarehouseRepository: RxWarehouseRepository,
    private val warehouseCacheRepository: WarehouseCacheRepository
) : FetchGoodsFromOrderUseCase.WarehouseRepository,
    RevertFetchGoodsUseCase.WarehouseRepository {
    fun initWarehouseCache(): Single<MutableIterable<WarehouseCacheModel>> {
        return rxWarehouseRepository.findAll()
            .toList()
            .flatMap { warehouseModelList: MutableList<WarehouseModel> ->
                Single.just(
                    warehouseCacheRepository.saveAll(
                        warehouseModelList.map { warehouseModel: WarehouseModel ->
                            WarehouseCacheModel(
                                productId = warehouseModel.productId,
                                amount = warehouseModel.amount,
                                isInStock = warehouseModel.isInStock,
                            )
                        }
                    )
                )
            }
    }

    override fun fetchGoodsByProductIds(productsWithAmount: Map<String, Int>): Single<Boolean> {
        return Flowable.fromIterable(
            productsWithAmount.map {
                warehouseCacheRepository.findByProductId(it.key.toLong())
            }
        )
            .toList()
            .flatMap {
                if (it.size == 0) return@flatMap Single.just(false)

                for (warehouseCacheModel in it) {
                    productsWithAmount[warehouseCacheModel.productId.toString()]?.let { requestAmount: Int ->
                        if (!warehouseCacheModel.isInStock || requestAmount > warehouseCacheModel.amount) {
                            return@flatMap Single.just(false)
                        } else {
                            warehouseCacheModel.amount -= requestAmount
                        }
                    }
                }

                return@flatMap Single.just(warehouseCacheRepository.saveAll(it))
                    .flatMap {
                        Single.just(true)
                    }
                    .onErrorReturn {
                        false
                    }
            }
    }

    override fun revert(productsWithAmount: Map<String, Int>): Single<Boolean> {
        return Flowable.fromIterable(
            productsWithAmount.map {
                warehouseCacheRepository.findByProductId(it.key.toLong())
            }
        )
            .toList()
            .flatMap {
                if (it.size == 0) return@flatMap Single.just(false)

                for (warehouseCacheModel in it) {
                    productsWithAmount[warehouseCacheModel.productId.toString()]?.let { requestAmount: Int ->
                        warehouseCacheModel.amount += requestAmount
                    }
                }

                return@flatMap Single.just(warehouseCacheRepository.saveAll(it))
                    .flatMap {
                        Single.just(true)
                    }
                    .onErrorReturn {
                        false
                    }
            }
    }
}
