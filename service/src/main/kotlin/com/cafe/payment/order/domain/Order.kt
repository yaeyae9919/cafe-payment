package com.cafe.payment.order.domain

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.order.OrderStatusTransitionException
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
    val itemIds: List<OrderItemId>,
    val totalAmount: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    /**
     * 낙관적 lock을 위한 version 필드
     * - 주문 상태의 정합성을 보장(상태가 덮어씌워진다던지 등)하기 위함이에요.
     * - 결제는 결제 서버가 멱등성 있게 처리해준다고 가정해요. (중복 결제 절대 일어나지 않음)
     */
    val version: Int,
) {
    fun isBuyer(buyerId: UserId): Boolean = this.buyerId == buyerId

    fun isNotPayComplete(): Boolean = !this.status.isPayComplete()

    fun payComplete(now: LocalDateTime): Order {
        val newStatus = OrderStatus.PAY_COMPLETED
        return modify(
            status = newStatus,
            now = now,
        )
    }

    fun payFailed(now: LocalDateTime): Order {
        val newStatus = OrderStatus.PAY_FAILED
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

    fun cancelProcessing(now: LocalDateTime): Order {
        val newStatus = OrderStatus.CANCEL_PROCESSING
        return modify(
            status = newStatus,
            now = now,
        )
    }

    fun cancelFailed(now: LocalDateTime): Order {
        val newStatus = OrderStatus.CANCEL_FAILED
        return modify(
            status = newStatus,
            now = now,
        )
    }

    fun cancelCompleted(now: LocalDateTime): Order {
        val newStatus = OrderStatus.CANCEL_COMPLETED
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
            itemIds = this.itemIds,
            totalAmount = this.totalAmount,
            createdAt = this.createdAt,
            updatedAt = now,
            version = this.version,
        )
    }

    companion object {
        fun create(
            id: OrderId,
            payId: PayId,
            buyerId: UserId,
            itemIds: List<OrderItemId>,
            totalAmount: BigDecimal,
            now: LocalDateTime = LocalDateTime.now(),
        ): Order {
            require(itemIds.isNotEmpty()) { "주문 상품이 비어있을 수 없습니다." }

            return Order(
                id = id,
                payId = payId,
                status = OrderStatus.PENDING,
                buyerId = buyerId,
                itemIds = itemIds,
                totalAmount = totalAmount,
                createdAt = now,
                updatedAt = now,
                version = 0,
            )
        }
    }
}

enum class OrderStatus {
    PENDING,

    // 결제 관련
    PAY_PROCESSING,
    PAY_FAILED,
    PAY_COMPLETED,

    // 취소 관련
    CANCEL_PROCESSING,
    CANCEL_FAILED,
    CANCEL_COMPLETED,
    ;

    fun isPayComplete() = this == PAY_COMPLETED

    fun canTransitionTo(newStatus: OrderStatus): Boolean {
        return when (this) {
            PENDING -> newStatus in setOf(PAY_PROCESSING, PAY_FAILED, PAY_COMPLETED)
            PAY_PROCESSING -> newStatus in setOf(PAY_PROCESSING, PAY_COMPLETED, PAY_FAILED) // 타임아웃 재시도 허용
            PAY_FAILED -> newStatus in setOf(PAY_PROCESSING, PAY_COMPLETED) // 재시도 가능
            PAY_COMPLETED -> newStatus in setOf(CANCEL_PROCESSING, CANCEL_FAILED, CANCEL_COMPLETED)
            CANCEL_PROCESSING -> newStatus in setOf(CANCEL_PROCESSING, CANCEL_COMPLETED, CANCEL_FAILED) // 타임아웃 재시도 허용
            CANCEL_FAILED -> newStatus in setOf(CANCEL_PROCESSING, CANCEL_COMPLETED) // 재시도 가능
            CANCEL_COMPLETED -> false // 최종 상태
        }
    }
}
