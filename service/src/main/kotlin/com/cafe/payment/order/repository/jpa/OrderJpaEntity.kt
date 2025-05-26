package com.cafe.payment.order.repository.jpa

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItemId
import com.cafe.payment.order.domain.OrderStatus
import com.cafe.payment.user.domain.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class OrderJpaEntity(
    @Id
    val id: Long,
    @Column(name = "pay_id", nullable = false)
    val payId: Long,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    val status: OrderStatus,
    @Column(name = "buyer_id", nullable = false)
    val buyerId: Long,
    @Column(name = "total_amount", nullable = false, length = 20)
    val totalAmount: String,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime,
    @Version
    val version: Int,
) {
    fun toDomain(itemIds: List<OrderItemId>): Order {
        return Order(
            id = OrderId(this.id),
            payId = PayId(this.payId),
            status = this.status,
            buyerId = UserId(this.buyerId),
            itemIds = itemIds,
            totalAmount = BigDecimal(this.totalAmount),
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            version = this.version,
        )
    }

    companion object {
        fun fromDomain(order: Order): OrderJpaEntity {
            return OrderJpaEntity(
                id = order.id.value,
                payId = order.payId.value,
                status = order.status,
                buyerId = order.buyerId.value,
                totalAmount = order.totalAmount.toString(),
                createdAt = order.createdAt,
                updatedAt = order.updatedAt,
                version = order.version,
            )
        }
    }
}
