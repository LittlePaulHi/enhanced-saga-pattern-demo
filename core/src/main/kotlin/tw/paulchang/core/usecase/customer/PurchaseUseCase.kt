package tw.paulchang.core.usecase.customer

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.customer.PurchaseRequestDto
import tw.paulchang.core.usecase.UseCase

class PurchaseUseCase(
    private val customerRepository: CustomerRepository
) : UseCase<PurchaseRequestDto, Boolean> {
    interface CustomerRepository {
        fun purchaseByCarts(customerId: Long, cartId: List<Long>): Single<Boolean>
    }

    override fun execute(request: PurchaseRequestDto): Single<Boolean> {
        return customerRepository.purchaseByCarts(request.customerId, request.cartIds)
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
