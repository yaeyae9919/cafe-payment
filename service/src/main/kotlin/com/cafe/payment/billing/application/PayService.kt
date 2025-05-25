package com.cafe.payment.billing.application

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.billing.domain.PayTransactionId
import com.cafe.payment.billing.external.PayClient
import com.cafe.payment.billing.external.PayResult
import com.cafe.payment.order.domain.Order
import org.springframework.stereotype.Service
import java.time.LocalDateTime

interface PayService {
    fun pay(order: Order): Result<PayResult>
}

@Service
class PayServiceImpl(
    private val payClient: PayClient,
) : PayService {
    override fun pay(order: Order): Result<PayResult> {
        if (order.totalAmount == 0.toBigDecimal()) {
            return Result.success(
                PayResult(
                    payId = PayId.ZERO_PAY_ID,
                    transactionId = PayTransactionId.ZERO_PAY_TRANSACTION_ID,
                    amount = order.totalAmount,
                    transactionAt = LocalDateTime.now(),
                ),
            )
        }

        return payClient.pay(
            orderId = order.id,
            totalAmount = order.totalAmount,
        )
    }
}
