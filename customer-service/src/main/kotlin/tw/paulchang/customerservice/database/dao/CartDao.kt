package tw.paulchang.customerservice.database.dao

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Component
import tw.paulchang.core.entity.customer.Cart
import tw.paulchang.customerservice.database.model.CartModel
import tw.paulchang.customerservice.database.model.toCart
import tw.paulchang.customerservice.database.repository.RxCartRepository
import java.time.Instant

@Component
class CartDao(
    private val rxCartRepository: RxCartRepository
) {
    fun addCart(cartModel: CartModel): Single<CartModel> {
        return rxCartRepository.save(cartModel)
    }

    fun getCartById(cartId: Long): Maybe<Cart> {
        return rxCartRepository.findById(cartId)
            .defaultIfEmpty(
                CartModel(
                    id = -1L,
                    productId = -1L,
                    quantity = -1,
                    createdAt = Instant.now()
                )
            )
            .flatMapMaybe {
                if (it.id == -1L && it.productId == -1L) {
                    Maybe.empty()
                } else {
                    Maybe.just(
                        it.toCart()
                    )
                }
            }
    }

    fun getAllCartsByIds(cartIds: List<Long>): Flowable<CartModel> {
        return rxCartRepository.findAllById(cartIds)
    }
}
