package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.Order
import org.springframework.stereotype.Repository

interface OrderRepository {
    fun save(order: Order): Order
}

@Repository
class NoOpOrderRepository : OrderRepository {
    override fun save(order: Order): Order {
        return order
    }
}
