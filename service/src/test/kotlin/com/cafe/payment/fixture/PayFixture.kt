package com.cafe.payment.fixture

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.billing.domain.PayTransactionId
import com.cafe.payment.billing.external.PayFailure
import com.cafe.payment.billing.external.PayResult
import net.bytebuddy.utility.RandomString
import java.math.BigDecimal
import java.time.LocalDateTime

object PayFixture {
    private var payId: Long = 1L

    fun generatePayId() = PayId(payId++)

    fun generatePayTransactionId() = PayTransactionId(RandomString.make(100))

    fun createSuccessPayResult(
        payId: PayId = generatePayId(),
        transactionId: PayTransactionId = generatePayTransactionId(),
        amount: BigDecimal = 1000.toBigDecimal(),
        transactionAt: LocalDateTime = LocalDateTime.now(),
    ) = Result.success(
        PayResult(
            payId = payId,
            transactionId = transactionId,
            amount = amount,
            transactionAt = transactionAt,
        ),
    )

    fun createFailPayResult(failure: PayFailure = PayFailure.InternalServerError()) = Result.failure<PayResult>(failure)
}
