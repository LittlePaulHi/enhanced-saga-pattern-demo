package tw.paulchang.warehouseservice.database.dao

import io.reactivex.rxjava3.core.Single
import mu.KLogging
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tw.paulchang.core.usecase.repository.WarehouseRepository
import tw.paulchang.warehouseservice.database.repository.RxWarehouseRepository

@Component
class WarehouseDao(
    private val rxWarehouseRepository: RxWarehouseRepository
) : WarehouseRepository {
    @Transactional
    override fun fetchGoodsByProductIds(productsWithAmount: Map<String, Int>): Single<Boolean> {
        logger.info { "warehouse-service: fetch goods by $productsWithAmount" }
        return rxWarehouseRepository.findAllByProductIdIn(
            productsWithAmount.map { it.key.toLong() }
        )
            .toList()
            .flatMap {
                for (warehouseModel in it) {
                    productsWithAmount[warehouseModel.productId.toString()]?.let { requestAmount: Int ->
                        if (!warehouseModel.isInStock || requestAmount > warehouseModel.amount) {
                            return@flatMap Single.just(false)
                        } else {
                            warehouseModel.amount -= requestAmount
                        }
                    }
                }

                return@flatMap rxWarehouseRepository.saveAll(it)
                    .lastOrError()
                    .flatMap {
                        Single.just(true)
                    }
                    .doOnError { err: Throwable ->
                        throw err
                    }
            }
    }

    @Transactional
    override fun revert(productsWithAmount: Map<String, Int>): Single<Boolean> {
        logger.info { "warehouse-service: [ROLLBACK] revert fetched goods by $productsWithAmount" }
        return rxWarehouseRepository.findAllByProductIdIn(
            productsWithAmount.map { it.key.toLong() }
        )
            .toList()
            .flatMap {
                for (warehouseModel in it) {
                    productsWithAmount[warehouseModel.productId.toString()]?.let { requestAmount: Int ->
                        warehouseModel.amount += requestAmount
                    }
                }

                return@flatMap rxWarehouseRepository.saveAll(it)
                    .lastOrError()
                    .flatMap {
                        Single.just(true)
                    }
                    .doOnError { err: Throwable ->
                        throw err
                    }
            }
    }

    companion object : KLogging()
}
