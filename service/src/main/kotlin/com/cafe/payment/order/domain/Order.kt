package com.cafe.payment.order.domain

import com.cafe.payment.order.InvalidOrderItemException
import com.cafe.payment.product.domain.Product
import com.cafe.payment.product.domain.ProductId
import com.cafe.payment.user.domain.UserId
import java.math.BigDecimal
import java.time.LocalDateTime

@JvmInline
value class OrderId(val value: Long) {
    override fun toString(): String {
        return value.toString()
    }
}

// 주문 내역
class Order private constructor(
    val id: OrderId,
    val buyerId: UserId,
    val items: List<OrderItem>,
    val createdAt: LocalDateTime,
) {
    val totalAmount = this.items.sumOf { it.totalAmount }

    companion object {
        fun create(
            id: OrderId,
            buyerId: UserId,
            items: List<OrderItem>,
            now: LocalDateTime = LocalDateTime.now(),
        ): Order {
            if (items.isEmpty()) throw InvalidOrderItemException.invalidOrderItems()

            return Order(
                id = id,
                buyerId = buyerId,
                items = items,
                createdAt = now,
            )
        }
    }
}

/**
 * 주문 상품 정보
 * 상품의 이름/금액은 변동될 수 있기 때문에 주문 시점의 가격을 저장해요.
 */
data class OrderItem(
    val productId: ProductId,
    val quantity: Int,
    // 주문 시점의 상품 이름 (스냅샷)
    val productName: String,
    // 주문 시점의 상품 단가 (스냅샷)
    val amount: BigDecimal,
) {
    val totalAmount: BigDecimal
        get() = amount.multiply(BigDecimal.valueOf(quantity.toLong()))

    companion object {
        fun create(
            product: Product,
            quantity: Int,
        ): OrderItem {
            if (quantity <= 0) throw InvalidOrderItemException.invalidQuantity()

            return OrderItem(
                productId = product.id,
                quantity = quantity,
                productName = product.name,
                amount = product.amount,
            )
        }
    }
}
