package com.cafe.payment.order.repository.jpa

import com.cafe.payment.billing.external.PayResult
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderPayHistory
import com.cafe.payment.order.domain.OrderStatus
import com.cafe.payment.order.repository.jpa.converter.PayResultConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "order_pay_histories")
class OrderPayHistoryJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Column(name = "order_id", nullable = false)
    val orderId: Long,
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 20)
    val orderStatus: OrderStatus,
    @Convert(converter = PayResultConverter::class)
    @Column(name = "pay_result", columnDefinition = "TEXT")
    val payResult: PayResult?,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun toDomain(): OrderPayHistory {
        return OrderPayHistory(
            orderId = OrderId(this.orderId),
            orderStatus = this.orderStatus,
            payResult = this.payResult,
        )
    }

    companion object {
        fun fromDomain(orderPayHistory: OrderPayHistory): OrderPayHistoryJpaEntity {
            return OrderPayHistoryJpaEntity(
                orderId = orderPayHistory.orderId.value,
                orderStatus = orderPayHistory.orderStatus,
                payResult = orderPayHistory.payResult,
            )
        }
    }
} 
