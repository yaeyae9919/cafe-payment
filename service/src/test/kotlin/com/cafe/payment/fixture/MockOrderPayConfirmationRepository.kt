package com.cafe.payment.fixture

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderPayConfirmation
import com.cafe.payment.order.repository.OrderPayConfirmationRepository

class MockOrderPayConfirmationRepository : OrderPayConfirmationRepository {
    private val confirmations = mutableMapOf<OrderId, OrderPayConfirmation>()

    override fun save(orderPayConfirmation: OrderPayConfirmation): OrderPayConfirmation {
        confirmations[orderPayConfirmation.orderId] = orderPayConfirmation
        return orderPayConfirmation
    }

    override fun findByOrderId(orderId: OrderId): OrderPayConfirmation? {
        return confirmations[orderId]
    }

    fun clear() {
        confirmations.clear()
    }
}
