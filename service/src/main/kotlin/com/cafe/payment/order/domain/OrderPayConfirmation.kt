package com.cafe.payment.order.domain

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.order.OrderConfirmationStatusException
import java.math.BigDecimal
import java.time.LocalDateTime

// 특정 주문의 승인/취소 확정 정보 모델
class OrderPayConfirmation private constructor(
    val orderId: OrderId,
    val totalAmount: BigDecimal,
    // 결제 정보 ID
    val payId: PayId?,
    // 주문 완료 시점
    val paidAt: LocalDateTime?,
    // 주문 취소 시점
    val canceledAt: LocalDateTime?,
) {
    var status: OrderConfirmationStatus = calculateStatus()

    private fun calculateStatus(): OrderConfirmationStatus {
        val paidCondition = paidAt != null && payId != null
        return when {
            paidCondition && canceledAt == null -> OrderConfirmationStatus.PAID
            paidCondition && canceledAt != null -> OrderConfirmationStatus.CANCELED
            else -> OrderConfirmationStatus.INIT
        }
    }

    // 주문 취소
    fun cancel(now: LocalDateTime = LocalDateTime.now()): OrderPayConfirmation {
        if (status.isCanceled) throw OrderConfirmationStatusException.alreadyCanceled()
        if (!(status.isPaid)) throw OrderConfirmationStatusException.notPaid()
        return modify(
            canceledAt = now,
        )
    }

    // 주문 완료
    fun paid(
        payId: PayId,
        now: LocalDateTime = LocalDateTime.now(),
    ): OrderPayConfirmation {
        if (status.isPaid) throw OrderConfirmationStatusException.alreadyPaid()

        return modify(
            payId = payId,
            paidAt = now,
        )
    }

    private fun modify(
        payId: PayId? = this.payId,
        paidAt: LocalDateTime? = this.paidAt,
        canceledAt: LocalDateTime? = this.canceledAt,
    ): OrderPayConfirmation {
        return OrderPayConfirmation(
            orderId = this.orderId,
            totalAmount = this.totalAmount,
            payId = payId,
            paidAt = paidAt,
            canceledAt = canceledAt,
        )
    }

    companion object {
        fun create(
            orderId: OrderId,
            totalAmount: BigDecimal,
        ) = OrderPayConfirmation(
            orderId = orderId,
            totalAmount = totalAmount,
            payId = null,
            paidAt = null,
            canceledAt = null,
        )
    }
}

enum class OrderConfirmationStatus {
    // 초기화
    INIT,

    // 주문 완료
    PAID,

    // 주문 취소
    CANCELED,

    ;

    val isPaid: Boolean get() = this == PAID
    val isCanceled: Boolean get() = this == CANCELED
}
