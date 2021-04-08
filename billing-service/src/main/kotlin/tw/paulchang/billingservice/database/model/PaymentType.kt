package tw.paulchang.billingservice.database.model

enum class PaymentType(val value: String) {
    CASH("CASH"),
    VISA("VISA"),
    MASTERCARD("MASTERCARD"),
}
