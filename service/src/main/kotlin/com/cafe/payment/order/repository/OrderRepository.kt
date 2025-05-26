package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import org.springframework.stereotype.Repository

interface OrderRepository {
    fun findById(orderId: OrderId): Order?

    fun save(order: Order): Order
}

@Repository
class InMemoryOrderRepository : OrderRepository {
    private val orders = mutableMapOf<OrderId, Order>()

    override fun findById(orderId: OrderId): Order? {
        return orders[orderId]
    }

    override fun save(order: Order): Order {
        orders[order.id] = order
        return order
    }
}
