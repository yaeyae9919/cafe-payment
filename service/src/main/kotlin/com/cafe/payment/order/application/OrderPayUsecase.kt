package com.cafe.payment.order.application

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.product.domain.ProductId
import com.cafe.payment.user.domain.UserId

interface OrderPayUsecase {
    /**
     * 주문 - 결제 준비
     * "준비" 절차가 없다면 타임아웃 등으로 인해 실제 결제가 실패했는지 / 성공했는지 여부를 모르는 상황에서
     * 비동기 결제 성공/실패 여부를 알 수 없다.
     */
    fun prepareOrder(
        buyerId: UserId,
        orderItems: List<OrderItem>,
    ): OrderId

    // 주문 및 결제
    fun orderPay(
        requesterId: UserId,
        orderId: OrderId,
    ): OrderPayResult

    fun orderPayRefund(
        requesterId: UserId,
        orderId: OrderId,
    ): OrderPayResult

    data class OrderItem(
        val productId: ProductId,
        val quantity: Int,
    )

    data class OrderPayResult(
        val orderId: OrderId,
        val status: OrderPayStatus,
    )

    enum class OrderPayStatus {
        SUCCESS, // 주문 성공
        FAILED, // 주문 실패
    }
}
