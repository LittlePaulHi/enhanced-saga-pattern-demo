package tw.paulchang.warehouseservice.controller

import io.reactivex.rxjava3.core.Single
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tw.paulchang.core.entity.warehouse.Product
import tw.paulchang.core.usecase.UseCaseExecutor
import tw.paulchang.core.usecase.warehouse.GetProductByIdUseCase

@RestController
@RequestMapping("/product")
class ProductController(
    private val useCaseExecutor: UseCaseExecutor,
    private val getProductByIdUseCase: GetProductByIdUseCase
) {
    @GetMapping("/{productId}")
    fun getProductById(@PathVariable("productId") productId: Long): Single<Product> {
        return useCaseExecutor(
            useCase = getProductByIdUseCase,
            request = productId
        )
    }
}
