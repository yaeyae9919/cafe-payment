package com.cafe.payment.fixture

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderPayHistory
import com.cafe.payment.order.repository.OrderPayHistoryRepository

class MockOrderPayHistoryRepository : OrderPayHistoryRepository {
    private val orderPayHistories = mutableMapOf<OrderId, MutableList<OrderPayHistory>>()

    override fun record(history: OrderPayHistory) {
        orderPayHistories.getOrPut(history.orderId) { mutableListOf() }.add(history)
    }

    fun clear() {
        orderPayHistories.clear()
    }
}
