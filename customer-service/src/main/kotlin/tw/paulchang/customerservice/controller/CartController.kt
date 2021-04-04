package tw.paulchang.customerservice.controller

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.customer.AddCartRequestDto
import tw.paulchang.core.entity.customer.Cart
import tw.paulchang.customerservice.database.model.CartModel
import tw.paulchang.customerservice.service.CartService

@RestController
@RequestMapping("/cart")
class CartController(
    private val cartService: CartService
) {
    @PostMapping("/addCart")
    fun addCart(@RequestBody addCartRequestDto: AddCartRequestDto): Single<CartModel> {
        return cartService.addCart(
            CartModel(
                id = null,
                productId = addCartRequestDto.productId,
                quantity = addCartRequestDto.quantity
            )
        )
    }

    @GetMapping("/{cartId}")
    fun getCartById(@PathVariable("cartId") cartId: Long): Maybe<Cart> {
        return cartService.getCartById(cartId)
    }

    @GetMapping
    fun getAllCartsByIds(@RequestBody cartIds: List<Long>): Flowable<CartModel> {
        return cartService.getAllCartsByIds(cartIds)
    }
}
