package com.cafe.payment.order.domain

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.order.InvalidOrderItemException
import com.cafe.payment.order.OrderStatusTransitionException
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

/**
 * 주문 내역
 */
class Order private constructor(
    val id: OrderId,
    val payId: PayId,
    /**
     * 가장 최신의 주문 상태
     * - 결제 지연 시, 후처리를 위한 상태값이 필요해요.
     * - 조회 성능 최적화
     * - 단, Order의 상태가 생김으로서 일관성을 보장하기 위해 락이 필요해졌어요.
     */
    val status: OrderStatus,
    val buyerId: UserId,
    val items: List<OrderItem>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    val totalAmount = this.items.sumOf { it.totalAmount }

    fun isBuyer(buyerId: UserId): Boolean = this.buyerId == buyerId

    fun payComplete(now: LocalDateTime): Order {
        val newStatus = OrderStatus.PAY_PROCESSING
        return modify(
            status = newStatus,
            now = now,
        )
    }

    fun payFailed(now: LocalDateTime): Order {
        val newStatus = OrderStatus.PAY_PROCESSING
        return modify(
            status = newStatus,
            now = now,
        )
    }

    fun payProcessing(now: LocalDateTime): Order {
        val newStatus = OrderStatus.PAY_PROCESSING
        return modify(
            status = newStatus,
            now = now,
        )
    }

    private fun modify(
        status: OrderStatus = this.status,
        now: LocalDateTime,
    ): Order {
        if (this.status.canTransitionTo(status).not()) {
            throw OrderStatusTransitionException.invalidOrderStatusTransition(
                from = this.status,
                to = status,
            )
        }

        return Order(
            id = this.id,
            payId = this.payId,
            status = status,
            buyerId = this.buyerId,
            items = this.items,
            createdAt = this.createdAt,
            updatedAt = now,
        )
    }

    companion object {
        fun create(
            id: OrderId,
            payId: PayId,
            buyerId: UserId,
            items: List<OrderItem>,
            now: LocalDateTime = LocalDateTime.now(),
        ): Order {
            if (items.isEmpty()) throw InvalidOrderItemException.invalidOrderItems()

            return Order(
                id = id,
                payId = payId,
                status = OrderStatus.PAY_PENDING,
                buyerId = buyerId,
                items = items,
                createdAt = now,
                updatedAt = now,
            )
        }
    }
}

enum class OrderStatus {
    // 결제 관련
    PAY_PENDING,
    PAY_PROCESSING,
    PAY_FAILED,
    PAY_COMPLETED,
    ;

    fun canTransitionTo(newStatus: OrderStatus): Boolean {
        return when (this) {
            PAY_PENDING -> newStatus in setOf(PAY_PROCESSING, PAY_FAILED, PAY_COMPLETED)
            PAY_PROCESSING -> newStatus in setOf(PAY_COMPLETED, PAY_FAILED)
            PAY_FAILED -> newStatus in setOf(PAY_PROCESSING, PAY_COMPLETED) // 재시도 가능
            PAY_COMPLETED -> false // 최종 상태
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
