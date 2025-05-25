package com.cafe.payment.billing.domain

@JvmInline
value class PayTransactionId(val value: String) {
    override fun toString(): String {
        return value
    }

    companion object {
        val ZERO_PAY_TRANSACTION_ID = PayTransactionId("ZERO_TX_1")
    }
}
