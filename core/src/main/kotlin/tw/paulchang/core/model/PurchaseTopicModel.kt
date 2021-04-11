package tw.paulchang.core.model

data class PurchaseTopicModel(
    val customerId: Long,
    val purchasedProductsWithAmount: Map<Long, Int>,
    val paymentType: String
)
