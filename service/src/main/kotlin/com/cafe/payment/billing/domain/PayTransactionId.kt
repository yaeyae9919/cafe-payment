package com.cafe.payment.billing.domain

@JvmInline
value class PayTransactionId(val value: String) {
    override fun toString(): String {
        return value
    }
}
