package tw.paulchang.orchestrator.webclient.quotacache

import io.reactivex.rxjava3.core.Single
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.adapter.rxjava.RxJava3Adapter
import reactor.core.publisher.Mono
import tw.paulchang.core.dto.billing.GetAllProductsByIdsResponseDto
import tw.paulchang.core.dto.warehouse.FetchGoodsRequestDto
import tw.paulchang.core.dto.warehouse.GetAllProductsByIdsRequestDto
import tw.paulchang.orchestrator.webclient.WorkflowStepStatus

@Component
class WarehouseCacheWebClient(
    @Qualifier("warehouse-quota-cache") private val warehouseQuotaCacheClient: WebClient,
) {
    private var stepStatus: WorkflowStepStatus = WorkflowStepStatus.PENDING

    fun getStatus(): WorkflowStepStatus {
        return stepStatus
    }

    fun getAllProductsByIds(
        getAllProductsByIdsRequestDto: GetAllProductsByIdsRequestDto
    ): Single<GetAllProductsByIdsResponseDto> {
        return RxJava3Adapter.monoToSingle(
            warehouseQuotaCacheClient
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
            warehouseQuotaCacheClient
                .post()
                .uri("/warehouse/quota-cache/fetchGoods")
                .body(BodyInserters.fromValue(fetchGoodsRequestDto))
                .exchangeToMono { clientResponse: ClientResponse ->
                    return@exchangeToMono if (clientResponse.statusCode().isError) {
                        stepStatus = WorkflowStepStatus.FAILED
                        Mono.just(false)
                    } else {
                        stepStatus = WorkflowStepStatus.COMPLETE
                        clientResponse.bodyToMono(Boolean::class.java)
                    }
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }

    fun revertFetchGoods(fetchGoodsRequestDto: FetchGoodsRequestDto): Single<Boolean> {
        return RxJava3Adapter.monoToSingle(
            warehouseQuotaCacheClient
                .post()
                .uri("/warehouse/quota-cache/revert/fetchGoods")
                .body(BodyInserters.fromValue(fetchGoodsRequestDto))
                .exchangeToMono { clientResponse: ClientResponse ->
                    return@exchangeToMono if (clientResponse.statusCode().isError) {
                        stepStatus = WorkflowStepStatus.FAILED
                        Mono.just(false)
                    } else {
                        stepStatus = WorkflowStepStatus.COMPLETE
                        clientResponse.bodyToMono(Boolean::class.java)
                    }
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }
}
