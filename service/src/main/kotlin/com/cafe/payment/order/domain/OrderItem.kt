package com.cafe.payment.order.domain

import com.cafe.payment.order.InvalidOrderItemException
import com.cafe.payment.product.domain.Product
import com.cafe.payment.product.domain.ProductId
import java.math.BigDecimal

@JvmInline
value class OrderItemId(val value: Long) {
    override fun toString(): String {
        return value.toString()
    }
}

/**
 * 주문 상품 정보
 * 상품의 이름/금액은 변동될 수 있기 때문에 주문 시점의 가격을 저장해요.
 */
data class OrderItem(
    val id: OrderItemId,
    val orderId: OrderId,
    val productId: ProductId,
    val quantity: Int,
    // 주문 시점의 상품 이름 (스냅샷)
    val productName: String,
    // 주문 시점의 상품 단가 (스냅샷)
    val amount: BigDecimal,
) {
    override fun equals(other: Any?) =
        when {
            this === other -> true
            other is OrderItem -> this.id == other.id
            else -> false
        }

    val totalAmount: BigDecimal
        get() = amount.multiply(BigDecimal.valueOf(quantity.toLong()))

    companion object {
        fun create(
            id: OrderItemId,
            orderId: OrderId,
            product: Product,
            quantity: Int,
        ): OrderItem {
            if (quantity <= 0) throw InvalidOrderItemException.invalidQuantity()

            return OrderItem(
                id = id,
                orderId = orderId,
                productId = product.id,
                quantity = quantity,
                productName = product.name,
                amount = product.amount,
            )
        }
    }
}
