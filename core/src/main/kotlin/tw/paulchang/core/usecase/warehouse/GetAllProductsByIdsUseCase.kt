package tw.paulchang.core.usecase.warehouse

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import tw.paulchang.core.dto.billing.GetAllProductsByIdsResponseDto
import tw.paulchang.core.dto.warehouse.GetAllProductsByIdsRequestDto
import tw.paulchang.core.entity.warehouse.Product
import tw.paulchang.core.usecase.UseCase

class GetAllProductsByIdsUseCase(
    private val productRepository: ProductRepository
) : UseCase<GetAllProductsByIdsRequestDto, GetAllProductsByIdsResponseDto> {
    interface ProductRepository {
        fun getAllProductsByIds(productIds: List<Long>): Flowable<Product>
    }

    override fun execute(request: GetAllProductsByIdsRequestDto): Single<GetAllProductsByIdsResponseDto> {
        return productRepository.getAllProductsByIds(request.productIds)
            .toList()
            .flatMap {
                Single.just(
                    GetAllProductsByIdsResponseDto(
                        products = it
                    )
                )
            }
            .onErrorResumeNext {
                Single.error(it)
            }
    }
}
