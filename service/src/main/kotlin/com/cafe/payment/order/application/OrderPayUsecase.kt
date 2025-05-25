package com.cafe.payment.order.application

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.user.domain.UserId

interface OrderPayUsecase {
    // 주문 및 결제
    fun orderAndPay(
        buyerId: UserId,
        orderItems: List<OrderItem>,
    ): OrderId
}
