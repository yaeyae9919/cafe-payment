package com.cafe.payment.order.repository.jpa

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.domain.OrderItemId
import com.cafe.payment.product.domain.ProductId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "order_items")
class OrderItemJpaEntity(
    @Id
    val id: Long,
    @Column(name = "order_id", nullable = false)
    val orderId: Long,
    @Column(name = "product_id", nullable = false)
    val productId: Long,
    @Column(nullable = false)
    val quantity: Int,
    @Column(name = "product_name", nullable = false, length = 100)
    val productName: String,
    @Column(nullable = false, length = 20)
    val amount: String,
) {
    fun toDomain(): OrderItem {
        return OrderItem(
            id = OrderItemId(this.id),
            orderId = OrderId(this.orderId),
            productId = ProductId(this.productId),
            quantity = this.quantity,
            productName = this.productName,
            amount = BigDecimal(this.amount),
        )
    }

    companion object {
        fun fromDomain(orderItem: OrderItem): OrderItemJpaEntity {
            return OrderItemJpaEntity(
                id = orderItem.id.value,
                orderId = orderItem.orderId.value,
                productId = orderItem.productId.value,
                quantity = orderItem.quantity,
                productName = orderItem.productName,
                amount = orderItem.amount.toString(),
            )
        }
    }
}
