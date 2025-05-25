package com.cafe.payment.billing.domain

@JvmInline
value class BillingTransactionId(val value: String) {
    override fun toString(): String {
        return value
    }
}
