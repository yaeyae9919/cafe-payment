package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.OrderPayHistory
import org.springframework.stereotype.Repository

interface OrderPayHistoryRepository {
    fun record(history: OrderPayHistory)
}

@Repository
class InMemoryOrderPayHistoryRepository : OrderPayHistoryRepository {
    private val orderPayHistory = mutableListOf<OrderPayHistory>()

    override fun record(history: OrderPayHistory) {
        orderPayHistory.add(history)
    }
}
