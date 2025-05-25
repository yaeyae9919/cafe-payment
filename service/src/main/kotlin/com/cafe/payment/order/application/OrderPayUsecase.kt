package com.cafe.payment.order.application

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.product.domain.ProductId
import com.cafe.payment.user.domain.UserId

interface OrderPayUsecase {
    // 주문 및 결제
    fun orderAndPay(
        buyerId: UserId,
        orderItems: List<OrderItem>,
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
