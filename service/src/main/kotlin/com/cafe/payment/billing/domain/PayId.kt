package com.cafe.payment.billing.domain

@JvmInline
value class PayId(val value: Long) {
    override fun toString(): String {
        return value.toString()
    }
}
