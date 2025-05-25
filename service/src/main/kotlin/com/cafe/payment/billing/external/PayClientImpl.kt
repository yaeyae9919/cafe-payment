package com.cafe.payment.billing.external

import com.cafe.payment.billing.domain.BillingTransactionId
import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.order.domain.OrderId
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random

/**
 * mocking 처리된 결제 서버 통신 구현체
 */
class PayClientImpl : PayClient {
    companion object {
        private const val TIMEOUT_THRESHOLD_MS = 3000L // 3초 이상이면 타임아웃으로 간주
        private const val PAYMENT_FAILURE_RATE = 0.2 // 20% 확률로 결제 실패
    }

    /**
     * 랜덤 지연 시간을 갖습니다.
     * 특정 확률로 처리 실패합니다.
     */
    override fun pay(
        orderId: OrderId,
        totalAmount: BigDecimal,
    ): Result<PayResult> {
        val delayMs = (1000L..10000L).random()

        // 타임아웃 체크
        val isTimeout = delayMs >= TIMEOUT_THRESHOLD_MS
        if (isTimeout) {
            Thread.sleep(delayMs)
            return Result.failure(
                PayFailure.TimeoutError(),
            )
        }

        // 결제 실패 확률 체크
        val isBillingFailed = Random.nextDouble() < PAYMENT_FAILURE_RATE
        if (isBillingFailed) {
            return Result.failure(
                PayFailure.PayServerError(),
            )
        }

        // 성공 응답
        return Result.success(
            PayResult(
                payId = generateBillingId(),
                transactionId = generateTransactionId(),
                amount = totalAmount,
                transactionAt = LocalDateTime.now(),
            ),
        )
    }

    override fun refund(
        payId: PayId,
        totalAmount: BigDecimal,
    ): Result<PayResult> {
        val delayMs = (1000L..10000L).random()

        // 타임아웃 체크
        val isTimeout = delayMs >= TIMEOUT_THRESHOLD_MS
        if (isTimeout) {
            Thread.sleep(delayMs)
            return Result.failure(
                PayFailure.TimeoutError(),
            )
        }

        // 결제 실패 확률 체크
        val isBillingFailed = Random.nextDouble() < PAYMENT_FAILURE_RATE
        if (isBillingFailed) {
            return Result.failure(
                PayFailure.PayServerError(),
            )
        }

        // 성공 응답
        return Result.success(
            PayResult(
                payId = payId,
                transactionId = generateTransactionId(),
                amount = totalAmount,
                transactionAt = LocalDateTime.now(),
            ),
        )
    }

    private fun generateBillingId(): PayId {
        return PayId(System.currentTimeMillis())
    }

    private fun generateTransactionId(): BillingTransactionId {
        return BillingTransactionId("TXN_${System.currentTimeMillis()}_${Random.nextInt(1000, 9999)}")
    }
}
