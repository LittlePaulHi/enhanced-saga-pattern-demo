package tw.paulchang.warehouseservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.dto.billing.GetAllProductsByIdsResponseDto
import tw.paulchang.core.dto.warehouse.GetAllProductsByIdsRequestDto
import tw.paulchang.core.entity.warehouse.Product
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.warehouse.GetAllProductsByIdsUseCase
import tw.paulchang.core.usecase.warehouse.GetProductByIdUseCase

@RestController
@RequestMapping("/product")
class ProductController(
    private val useCaseExecutor: UseCaseExecutor,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getAllProductsByIdsUseCase: GetAllProductsByIdsUseCase
) {
    @GetMapping("/{productId}")
    fun getProductById(@PathVariable("productId") productId: Long): Single<Product> {
        return useCaseExecutor(
            useCase = getProductByIdUseCase,
            request = productId
        )
    }

    @PostMapping("/getAllByIds")
    fun getAllProductsByIds(
        @RequestBody getAllProductsByIdsRequestDto: GetAllProductsByIdsRequestDto
    ): Single<GetAllProductsByIdsResponseDto> {
        return useCaseExecutor(
            useCase = getAllProductsByIdsUseCase,
            request = getAllProductsByIdsRequestDto
        )
    }
}
