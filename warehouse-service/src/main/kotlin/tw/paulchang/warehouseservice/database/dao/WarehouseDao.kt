package tw.paulchang.warehouseservice.database.dao

import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tw.paulchang.core.usecase.repository.WarehouseRepository
import tw.paulchang.warehouseservice.database.model.WarehouseModel
import tw.paulchang.warehouseservice.database.repository.RxWarehouseRepository

@Component
class WarehouseDao(
    private val rxWarehouseRepository: RxWarehouseRepository
) : WarehouseRepository {
    @Transactional
    override fun fetchGoodsByProductId(productId: Long, amount: Int): Single<Boolean> {
        return rxWarehouseRepository.findById(productId)
            .defaultIfEmpty(
                WarehouseModel(
                    id = -1,
                    productId = -1,
                    isInStock = false,
                    amount = -1
                )
            )
            .flatMap {
                return@flatMap if (!it.isInStock || it.amount < amount) {
                    Single.just(false)
                } else {
                    it.amount -= amount
                    rxWarehouseRepository.save(it)
                        .flatMap {
                            Single.just(true)
                        }
                        .doOnError { err: Throwable ->
                            throw err
                        }
                }
            }
    }

    @Transactional
    override fun revert(productId: Long, amount: Int): Single<Boolean> {
        return rxWarehouseRepository.findById(productId)
            .toSingle()
            .flatMap {
                it.amount += amount
                return@flatMap rxWarehouseRepository.save(it)
                    .flatMap {
                        Single.just(true)
                    }
                    .doOnError { err: Throwable ->
                        throw err
                    }
            }
    }
}
