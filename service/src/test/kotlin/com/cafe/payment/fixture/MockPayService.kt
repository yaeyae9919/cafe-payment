package com.cafe.payment.fixture

import com.cafe.payment.billing.application.PayService
import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.billing.external.PayResult
import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import java.time.LocalDateTime

class MockPayService : PayService {
    var payId: PayId = PayFixture.generatePayId()
    var payResult: Result<PayResult> =
        Result.success(
            PayResult(
                payId,
                PayFixture.generatePayTransactionId(),
                1000.toBigDecimal(),
                LocalDateTime.now(),
            ),
        )

    fun configurePayResult(result: Result<PayResult>) {
        payResult = result
    }

    override fun obtainPayId(orderId: OrderId): PayId {
        return payId
    }

    override fun pay(order: Order): Result<PayResult> {
        return payResult
    }

    override fun refund(order: Order): Result<PayResult> {
        return payResult
    }
}
