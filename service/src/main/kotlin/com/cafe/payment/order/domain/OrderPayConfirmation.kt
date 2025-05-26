package com.cafe.payment.order.domain

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.order.OrderPayConfirmationStatusException
import java.math.BigDecimal
import java.time.LocalDateTime

// 특정 주문의 승인/취소 확정 정보 모델
class OrderPayConfirmation private constructor(
    val orderId: OrderId,
    val totalAmount: BigDecimal,
    // 결제 정보 ID
    val payId: PayId,
    // 주문 완료 시점
    val paidAt: LocalDateTime,
    // 주문 취소 시점
    val canceledAt: LocalDateTime?,
) {
    var status: OrderConfirmationStatus = calculateStatus()

    fun isCanceled(): Boolean = this.status.isCanceled

    private fun calculateStatus(): OrderConfirmationStatus {
        return when (canceledAt == null) {
            true -> OrderConfirmationStatus.PAID
            false -> OrderConfirmationStatus.CANCELED
        }
    }

    // 주문 취소
    fun cancel(now: LocalDateTime = LocalDateTime.now()): OrderPayConfirmation {
        if (status.isCanceled) throw OrderPayConfirmationStatusException.alreadyCanceled()
        if (!(status.isPaid)) throw OrderPayConfirmationStatusException.notPaid()
        return modify(
            canceledAt = now,
        )
    }

    private fun modify(canceledAt: LocalDateTime? = this.canceledAt): OrderPayConfirmation {
        return OrderPayConfirmation(
            orderId = this.orderId,
            totalAmount = this.totalAmount,
            payId = this.payId,
            paidAt = this.paidAt,
            canceledAt = canceledAt,
        )
    }

    companion object {
        // 주문 완료
        fun paid(
            payId: PayId,
            paidAt: LocalDateTime,
            order: Order,
        ) = OrderPayConfirmation(
            orderId = order.id,
            totalAmount = order.totalAmount,
            payId = payId,
            paidAt = paidAt,
            canceledAt = null,
        )
    }
}

enum class OrderConfirmationStatus {
    // 주문 완료
    PAID,

    // 주문 취소
    CANCELED,

    ;

    val isPaid: Boolean get() = this == PAID
    val isCanceled: Boolean get() = this == CANCELED
}
