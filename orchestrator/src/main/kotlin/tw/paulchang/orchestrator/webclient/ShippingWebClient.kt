package tw.paulchang.orchestrator.webclient

import io.reactivex.rxjava3.core.Single
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.adapter.rxjava.RxJava3Adapter
import tw.paulchang.core.dto.shipping.DispatchShippingRequestDto
import tw.paulchang.core.entity.shipping.Shipping

@Component
class ShippingWebClient(
    @Qualifier("shipping") private val shippingClient: WebClient,
) {
    private var stepStatus: WorkflowStepStatus = WorkflowStepStatus.PENDING

    fun getStatus(): WorkflowStepStatus {
        return stepStatus
    }

    fun dispatch(dispatchShippingRequestDto: DispatchShippingRequestDto): Single<Shipping> {
        return RxJava3Adapter.monoToSingle(
            shippingClient
                .post()
                .uri("/shipping/dispatch")
                .body(BodyInserters.fromValue(dispatchShippingRequestDto))
                .retrieve()
                .bodyToMono<Shipping>()
                .doOnNext { resp ->
                    stepStatus =
                        if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }
}
