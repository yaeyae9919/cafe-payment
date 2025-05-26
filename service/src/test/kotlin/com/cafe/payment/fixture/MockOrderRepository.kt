package com.cafe.payment.fixture

import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.repository.OrderRepository

class MockOrderRepository : OrderRepository {
    private val orders = mutableMapOf<OrderId, Order>()

    override fun findById(orderId: OrderId): Order? {
        return orders[orderId]
    }

    override fun save(order: Order): Order {
        orders[order.id] = order
        return order
    }

    fun clear() {
        orders.clear()
    }
}
