package tw.paulchang.core.usecase.repository

import tw.paulchang.core.usecase.order.InitOrderUseCase
import tw.paulchang.core.usecase.order.PlaceOrderUseCase

interface OrderRepository : InitOrderUseCase.OrderRepository, PlaceOrderUseCase.OrderRepository
