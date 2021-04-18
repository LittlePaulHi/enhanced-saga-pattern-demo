package tw.paulchang.orchestrator.webclient

import io.reactivex.rxjava3.core.Single
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.adapter.rxjava.RxJava3Adapter
import tw.paulchang.core.dto.billing.CreateBillingRequestDto
import tw.paulchang.core.dto.billing.PaymentPayRequestDto
import tw.paulchang.core.dto.billing.ValidatePaymentRequestDto
import tw.paulchang.core.entity.billing.Billing
import tw.paulchang.core.entity.billing.Payment

@Component
class BillingWebClient(
    @Qualifier("billing") private val billingClient: WebClient,
) {
    private var stepStatus: WorkflowStepStatus = WorkflowStepStatus.PENDING

    fun getStatus(): WorkflowStepStatus {
        return stepStatus
    }

    fun createBilling(createBillingRequestDto: CreateBillingRequestDto): Single<Billing> {
        return RxJava3Adapter.monoToSingle(
            billingClient
                .post()
                .uri("/billing/create")
                .body(BodyInserters.fromValue(createBillingRequestDto))
                .retrieve()
                .bodyToMono<Billing>()
                .doOnNext { resp ->
                    stepStatus =
                        if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }

    fun validate(validatePaymentRequestDto: ValidatePaymentRequestDto): Single<Boolean> {
        return RxJava3Adapter.monoToSingle(
            billingClient
                .post()
                .uri("/payment/validate")
                .body(BodyInserters.fromValue(validatePaymentRequestDto))
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

    fun pay(paymentPayRequestDto: PaymentPayRequestDto): Single<Payment> {
        return RxJava3Adapter.monoToSingle(
            billingClient
                .post()
                .uri("/payment/pay")
                .body(BodyInserters.fromValue(paymentPayRequestDto))
                .retrieve()
                .bodyToMono<Payment>()
                .doOnNext { resp ->
                    stepStatus =
                        if (resp != null) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED
                }
                .doOnError {
                    stepStatus = WorkflowStepStatus.FAILED
                }
        )
    }

    fun revertPay(paymentPayRequestDto: PaymentPayRequestDto): Single<Payment> {
        return RxJava3Adapter.monoToSingle(
            billingClient
                .post()
                .uri("/payment/revert/pay")
                .body(BodyInserters.fromValue(paymentPayRequestDto))
                .retrieve()
                .bodyToMono<Payment>()
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
