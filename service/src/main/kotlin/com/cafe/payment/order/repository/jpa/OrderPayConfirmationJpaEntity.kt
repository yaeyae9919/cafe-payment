package com.cafe.payment.order.repository.jpa

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderPayConfirmation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "order_pay_confirmations")
class OrderPayConfirmationJpaEntity(
    @Id
    @Column(name = "order_id")
    val orderId: Long,
    @Column(name = "total_amount", nullable = false, length = 20)
    val totalAmount: String,
    @Column(name = "pay_id", nullable = false)
    val payId: Long,
    @Column(name = "paid_at", nullable = false)
    val paidAt: LocalDateTime,
    @Column(name = "canceled_at")
    val canceledAt: LocalDateTime?,
) {
    fun toDomain(): OrderPayConfirmation {
        return OrderPayConfirmation(
            orderId = OrderId(this.orderId),
            totalAmount = BigDecimal(this.totalAmount),
            payId = PayId(this.payId),
            paidAt = this.paidAt,
            canceledAt = this.canceledAt,
        )
    }

    companion object {
        fun fromDomain(orderPayConfirmation: OrderPayConfirmation): OrderPayConfirmationJpaEntity {
            return OrderPayConfirmationJpaEntity(
                orderId = orderPayConfirmation.orderId.value,
                totalAmount = orderPayConfirmation.totalAmount.toString(),
                payId = orderPayConfirmation.payId.value,
                paidAt = orderPayConfirmation.paidAt,
                canceledAt = orderPayConfirmation.canceledAt,
            )
        }
    }
}
