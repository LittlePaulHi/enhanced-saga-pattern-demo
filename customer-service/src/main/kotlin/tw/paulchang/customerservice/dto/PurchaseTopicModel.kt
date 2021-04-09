package tw.paulchang.customerservice.dto

data class PurchaseTopicModel(
    val customerId: Long,
    val purchasedProductsWithAmount: Map<Long, Int>,
    val paymentType: String
)
