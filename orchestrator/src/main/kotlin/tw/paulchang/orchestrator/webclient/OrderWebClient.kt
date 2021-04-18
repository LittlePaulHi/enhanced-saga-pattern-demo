package tw.paulchang.orchestrator.webclient

import io.reactivex.rxjava3.core.Single
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.adapter.rxjava.RxJava3Adapter
import tw.paulchang.core.dto.order.InitOrderRequestDto
import tw.paulchang.core.dto.order.PlaceOrderRequestDto
import tw.paulchang.core.entity.order.Order

@Component
class OrderWebClient(
    @Qualifier("order") private val orderClient: WebClient,
) {
    private var stepStatus: WorkflowStepStatus = WorkflowStepStatus.PENDING

    fun getStatus(): WorkflowStepStatus {
        return stepStatus
    }

    fun initOrder(initOrderRequestDto: InitOrderRequestDto): Single<Order> {
        return RxJava3Adapter.monoToSingle(
            orderClient
                .post()
                .uri("/order/init")
                .body(BodyInserters.fromValue(initOrderRequestDto))
                .retrieve()
                .bodyToMono<Order>()
                .doOnNext { resp ->
                    stepStatus =
                        if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }

    fun placeOrder(placeOrderRequestDto: PlaceOrderRequestDto): Single<Order> {
        return RxJava3Adapter.monoToSingle(
            orderClient
                .post()
                .uri("/order/place")
                .body(BodyInserters.fromValue(placeOrderRequestDto))
                .retrieve()
                .bodyToMono<Order>()
                .doOnNext { resp ->
                    stepStatus = if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }
}
