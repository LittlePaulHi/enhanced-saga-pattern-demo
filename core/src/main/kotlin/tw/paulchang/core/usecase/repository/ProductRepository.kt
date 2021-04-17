package tw.paulchang.core.usecase.repository

import tw.paulchang.core.usecase.warehouse.GetAllProductsByIdsUseCase
import tw.paulchang.core.usecase.warehouse.GetProductByIdUseCase

interface ProductRepository :
    GetProductByIdUseCase.ProductRepository,
    GetAllProductsByIdsUseCase.ProductRepository
