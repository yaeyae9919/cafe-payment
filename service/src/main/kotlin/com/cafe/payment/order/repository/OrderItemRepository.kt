package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.OrderItem
import org.springframework.stereotype.Repository

interface OrderItemRepository {
    fun saveAll(orderItems: List<OrderItem>)
}

@Repository
class InMemoryOrderItemRepository : OrderItemRepository {
    private val orderItems = mutableListOf<OrderItem>()

    override fun saveAll(orderItems: List<OrderItem>) {
        this.orderItems.addAll(orderItems)
    }
}
