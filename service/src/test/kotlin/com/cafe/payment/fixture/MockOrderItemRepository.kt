package com.cafe.payment.fixture

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.repository.OrderItemRepository

class MockOrderItemRepository : OrderItemRepository {
    private val orderItems = mutableMapOf<OrderId, MutableList<OrderItem>>()

    override fun saveAll(orderItems: List<OrderItem>): List<OrderItem> {
        orderItems.forEach { orderItem ->
            this.orderItems.getOrPut(orderItem.orderId) { mutableListOf() }.add(orderItem)
        }
        return orderItems
    }

    override fun findByOrderId(orderId: OrderId): List<OrderItem> {
        return orderItems[orderId] ?: emptyList()
    }

    fun clear() {
        orderItems.clear()
    }
}
