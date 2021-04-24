package tw.paulchang.core.model

data class FinishedBuyEventTopicModel(
    // for warehouse-service' fetchGoods
    val productsWithAmount: Map<String, Int>,
    // for billing-service' payment pay
    val customerId: Long,
    val paymentType: String,
    val amount: Int,
)
