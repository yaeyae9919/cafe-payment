package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.Order

interface OrderRepository {
    fun save(order: Order)
}
