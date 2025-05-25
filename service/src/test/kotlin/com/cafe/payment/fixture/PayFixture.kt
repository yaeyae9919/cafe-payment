package com.cafe.payment.fixture

import com.cafe.payment.billing.domain.PayId

object PayFixture {
    private var payId: Long = 1L

    fun generatePayId() = PayId(payId++)
}
