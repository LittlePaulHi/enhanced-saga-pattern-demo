package tw.paulchang.core.usecase.repository

import tw.paulchang.core.usecase.billing.AddPaymentUseCase
import tw.paulchang.core.usecase.billing.PaymentPayUseCase
import tw.paulchang.core.usecase.billing.ValidatePaymentUseCase

interface PaymentRepository :
    AddPaymentUseCase.PaymentRepository,
    ValidatePaymentUseCase.PaymentRepository,
    PaymentPayUseCase.PaymentRepository
