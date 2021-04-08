package tw.paulchang.core.usecase.shipping

import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.shipping.DispatchShippingRequestDto
import tw.paulchang.core.entity.shipping.Shipping
import tw.paulchang.core.usecase.UseCase

class DispatchShippingUseCase(
    private val shippingRepository: ShippingRepository
) : UseCase<DispatchShippingRequestDto, Shipping> {
    interface ShippingRepository {
        fun dispatch(
            orderId: Long,
            shippingType: String,
        ): Single<Shipping>
    }

    override fun execute(request: DispatchShippingRequestDto): Single<Shipping> {
        return shippingRepository.dispatch(
            orderId = request.orderId,
            shippingType = request.shippingType
        )
    }
}
