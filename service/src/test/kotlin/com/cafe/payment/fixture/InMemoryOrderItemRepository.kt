package com.cafe.payment.fixture

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.domain.OrderItemId
import com.cafe.payment.order.repository.OrderItemRepository

class InMemoryOrderItemRepository : OrderItemRepository {
    private val orderItems = mutableMapOf<OrderItemId, OrderItem>()

    override fun saveAll(orderItems: List<OrderItem>): List<OrderItem> {
        orderItems.forEach { this.orderItems[it.id] = it }
        return orderItems
    }

    override fun findByOrderId(orderId: OrderId): List<OrderItem> {
        return orderItems.values.filter { it.orderId == orderId }
    }
}
