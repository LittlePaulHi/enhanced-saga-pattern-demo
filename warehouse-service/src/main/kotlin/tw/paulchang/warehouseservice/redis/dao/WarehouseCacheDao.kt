package tw.paulchang.warehouseservice.redis.dao

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import mu.KLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.stereotype.Component
import reactor.adapter.rxjava.RxJava3Adapter
import reactor.core.publisher.Flux
import tw.paulchang.core.usecase.warehouse.FetchGoodsFromOrderUseCase
import tw.paulchang.core.usecase.warehouse.RevertFetchGoodsUseCase
import tw.paulchang.warehouseservice.database.model.WarehouseModel
import tw.paulchang.warehouseservice.database.repository.RxWarehouseRepository
import tw.paulchang.warehouseservice.redis.model.WarehouseCacheModel
import java.util.UUID
import javax.persistence.LockModeType

@Component
class WarehouseCacheDao(
    private val rxWarehouseRepository: RxWarehouseRepository,
    private val warehouseReactiveRedisOperations: ReactiveRedisOperations<String, WarehouseCacheModel>,
) : FetchGoodsFromOrderUseCase.WarehouseRepository,
    RevertFetchGoodsUseCase.WarehouseRepository {

    @EventListener(ApplicationReadyEvent::class)
    fun initWarehouseCache(): Completable {
        return rxWarehouseRepository.findAll()
            .toList()
            .flatMapCompletable { warehouseModelList: MutableList<WarehouseModel> ->
                RxJava3Adapter.fluxToFlowable(
                    Flux.fromIterable(warehouseModelList)
                        .map { warehouseModel: WarehouseModel ->
                            WarehouseCacheModel(
                                id = UUID.nameUUIDFromBytes(
                                    warehouseModel.productId.toString().toByteArray()
                                ).toString(),
                                productId = warehouseModel.productId,
                                amount = warehouseModel.amount,
                                isInStock = warehouseModel.isInStock,
                            )
                        }
                        .flatMap { warehouseCacheModel: WarehouseCacheModel ->
                            warehouseReactiveRedisOperations.opsForValue().set(
                                warehouseCacheModel.id,
                                warehouseCacheModel
                            )
                        }
                )
                    .ignoreElements()
            }
    }

    @Lock(LockModeType.WRITE)
    override fun fetchGoodsByProductIds(productsWithAmount: Map<String, Int>): Single<Boolean> {
        logger.info { "warehouse-service-quota-cache: fetch goods by $productsWithAmount" }
        return RxJava3Adapter.monoToSingle(
            warehouseReactiveRedisOperations.opsForValue().multiGet(
                productsWithAmount.map {
                    UUID.nameUUIDFromBytes(
                        it.key.toByteArray()
                    ).toString()
                }
            )
        )
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

                return@flatMap RxJava3Adapter.monoToSingle(
                    warehouseReactiveRedisOperations.opsForValue().multiSet(
                        it.map { warehouseCacheModel: WarehouseCacheModel ->
                            warehouseCacheModel.id to warehouseCacheModel
                        }.toMap()
                    )
                )
            }
    }

    @Lock(LockModeType.WRITE)
    override fun revert(productsWithAmount: Map<String, Int>): Single<Boolean> {
        logger.info { "warehouse-service-quota-cache: compensate fetched goods by $productsWithAmount" }
        return RxJava3Adapter.monoToSingle(
            warehouseReactiveRedisOperations.opsForValue().multiGet(
                productsWithAmount.map {
                    UUID.nameUUIDFromBytes(
                        it.key.toByteArray()
                    ).toString()
                }
            )
        )
            .flatMap {
                if (it.size == 0) return@flatMap Single.just(false)

                for (warehouseCacheModel in it) {
                    productsWithAmount[warehouseCacheModel.productId.toString()]?.let { requestAmount: Int ->
                        warehouseCacheModel.amount += requestAmount
                    }
                }

                return@flatMap RxJava3Adapter.monoToSingle(
                    warehouseReactiveRedisOperations.opsForValue().multiSet(
                        it.map { warehouseCacheModel: WarehouseCacheModel ->
                            warehouseCacheModel.id to warehouseCacheModel
                        }.toMap()
                    )
                )
            }
    }

    companion object : KLogging()
}
