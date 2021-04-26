package tw.paulchang.core.model

object KafkaTopics {
    const val PURCHASE = "purchase"
    const val PURCHASE_VIA_QUOTA_CACHE = "purchaseViaQuotaCache"
    const val COMPENSATE_WAREHOUSE_GOODS = "compensateWarehouseGoods"
    const val COMPENSATE_PAYMENT = "compensatePayment"
    const val FINISHED_BUY_EVENT = "finishedBuyEvent"
}
