package tw.paulchang.orchestrator.webclient

import io.reactivex.rxjava3.core.Single
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.adapter.rxjava.RxJava3Adapter
import tw.paulchang.core.dto.billing.GetAllProductsByIdsResponseDto
import tw.paulchang.core.dto.warehouse.FetchGoodsRequestDto
import tw.paulchang.core.dto.warehouse.GetAllProductsByIdsRequestDto
import tw.paulchang.core.entity.warehouse.Product

@Component
class WarehouseWebClient(
    @Qualifier("warehouse") private val warehouseClient: WebClient,
) {
    private var stepStatus: WorkflowStepStatus = WorkflowStepStatus.PENDING

    fun getStatus(): WorkflowStepStatus {
        return stepStatus
    }

    fun getProductById(productId: Long): Single<Product> {
        return RxJava3Adapter.monoToSingle(
            warehouseClient
                .get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono<Product>()
                .doOnNext { resp ->
                    stepStatus =
                        if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }

    fun getAllProductsByIds(
        getAllProductsByIdsRequestDto: GetAllProductsByIdsRequestDto
    ): Single<GetAllProductsByIdsResponseDto> {
        return RxJava3Adapter.monoToSingle(
            warehouseClient
                .post()
                .uri("/product/getAllByIds")
                .body(BodyInserters.fromValue(getAllProductsByIdsRequestDto))
                .retrieve()
                .bodyToMono<GetAllProductsByIdsResponseDto>()
                .doOnNext { resp ->
                    stepStatus =
                        if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }

    fun fetchGoods(fetchGoodsRequestDto: FetchGoodsRequestDto): Single<Boolean> {
        return RxJava3Adapter.monoToSingle(
            warehouseClient
                .post()
                .uri("/warehouse/fetchGoods")
                .body(BodyInserters.fromValue(fetchGoodsRequestDto))
                .retrieve()
                .bodyToMono<Boolean>()
                .doOnNext { resp ->
                    stepStatus =
                        if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }

    fun revertFetchGoods(fetchGoodsRequestDto: FetchGoodsRequestDto): Single<Boolean> {
        return RxJava3Adapter.monoToSingle(
            warehouseClient
                .post()
                .uri("/warehouse/revert/fetchGoods")
                .body(BodyInserters.fromValue(fetchGoodsRequestDto))
                .retrieve()
                .bodyToMono<Boolean>()
                .doOnNext { resp ->
                    stepStatus =
                        if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
        )
    }
}
