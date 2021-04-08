package tw.paulchang.shippingservice.database.dao

import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import tw.paulchang.core.entity.shipping.Shipping
import tw.paulchang.core.usecase.repository.ShippingRepository
import tw.paulchang.shippingservice.database.model.ShippingModel
import tw.paulchang.shippingservice.database.model.toShipping
import tw.paulchang.shippingservice.database.repository.RxShippingRepository
import java.time.Instant

@Component
class ShippingDao(
    private val rxShippingRepository: RxShippingRepository
) : ShippingRepository {
    @Transactional
    override fun dispatch(orderId: Long, shippingType: String): Single<Shipping> {
        return rxShippingRepository.save(
            ShippingModel(
                id = null,
                orderId = orderId,
                shippingType = shippingType,
                createdAt = Instant.now()
            )
        )
            .flatMap {
                Single.just(it.toShipping())
            }
    }
}
