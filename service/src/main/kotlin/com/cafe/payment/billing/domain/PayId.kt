package com.cafe.payment.billing.domain

@JvmInline
value class PayId(val value: Long) {
    override fun toString(): String {
        return value.toString()
    }

    companion object {
        val ZERO_PAY_ID = PayId(0)
    }
}
