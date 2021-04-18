package tw.paulchang.core.usecase.repository

import tw.paulchang.core.usecase.warehouse.FetchGoodsFromOrderUseCase
import tw.paulchang.core.usecase.warehouse.RevertFetchGoodsUseCase

interface WarehouseRepository :
    FetchGoodsFromOrderUseCase.WarehouseRepository,
    RevertFetchGoodsUseCase.WarehouseRepository
