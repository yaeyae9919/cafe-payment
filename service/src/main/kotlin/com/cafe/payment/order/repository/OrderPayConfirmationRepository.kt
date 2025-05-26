package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderPayConfirmation
import org.springframework.stereotype.Repository

interface OrderPayConfirmationRepository {
    fun save(orderPayConfirmation: OrderPayConfirmation): OrderPayConfirmation

    fun findByOrderId(orderId: OrderId): OrderPayConfirmation?
}

@Repository
class NoOpOrderPayConfirmationRepository : OrderPayConfirmationRepository {
    private val confirmations = mutableMapOf<OrderId, OrderPayConfirmation>()

    override fun save(orderPayConfirmation: OrderPayConfirmation): OrderPayConfirmation {
        confirmations[orderPayConfirmation.orderId] = orderPayConfirmation
        return orderPayConfirmation
    }

    override fun findByOrderId(orderId: OrderId): OrderPayConfirmation? {
        return confirmations[orderId]
    }
}
