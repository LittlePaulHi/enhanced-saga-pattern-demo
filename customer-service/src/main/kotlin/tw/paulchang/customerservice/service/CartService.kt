package tw.paulchang.customerservice.service

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import org.springframework.stereotype.Service
import tw.paulchang.core.entity.customer.Cart
import tw.paulchang.customerservice.database.dao.CartDao
import tw.paulchang.customerservice.database.model.CartModel

@Service
class CartService(
    private val cartDao: CartDao
) {
    fun addCart(cartModel: CartModel): Single<CartModel> {
        return cartDao.addCart(cartModel)
    }

    fun getCartById(cartId: Long): Maybe<Cart> {
        return cartDao.getCartById(cartId)
    }

    fun getAllCartsByIds(cartIds: List<Long>): Flowable<CartModel> {
        return cartDao.getAllCartsByIds(cartIds)
    }
}
