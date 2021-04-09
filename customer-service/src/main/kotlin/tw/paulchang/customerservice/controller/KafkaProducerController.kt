package tw.paulchang.customerservice.controller

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import mu.KLogging
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.customer.PurchaseRequestDto
import tw.paulchang.customerservice.database.dao.CartDao
import tw.paulchang.customerservice.database.model.CartModel
import tw.paulchang.customerservice.dto.PurchaseTopicModel
import tw.paulchang.customerservice.service.PurchaseProducerService
import java.util.concurrent.Executors

@RestController
@RequestMapping("/customer")
class KafkaProducerController(
    private val cartDao: CartDao,
    private val producerService: PurchaseProducerService,
) {
    val scheduler: Scheduler = Schedulers.from(Executors.newFixedThreadPool(10))

    @PostMapping("/purchase")
    fun purchase(
        @RequestBody purchaseRequestDto: PurchaseRequestDto
    ): Maybe<ListenableFuture<SendResult<String, PurchaseTopicModel>>> {
        return cartDao.getAllCartsByIds(purchaseRequestDto.cartIds)
            .observeOn(scheduler) // switch from db's thread
            .toList()
            .flatMapMaybe { it: MutableList<CartModel>? ->
                return@flatMapMaybe it?.let { carts ->
                    producerService.sendPurchaseRecordToKafka(
                        PurchaseTopicModel(
                            customerId = purchaseRequestDto.customerId,
                            purchasedProductsWithAmount = carts.associate { it.productId to it.quantity },
                            paymentType = purchaseRequestDto.paymentType,
                        )
                    )
                } ?: Maybe.empty()
            }
    }

    companion object : KLogging()
}
