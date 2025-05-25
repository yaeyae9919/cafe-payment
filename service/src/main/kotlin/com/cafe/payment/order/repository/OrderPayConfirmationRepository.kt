package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.OrderPayConfirmation

interface OrderPayConfirmationRepository {
    fun save(orderPayConfirmation: OrderPayConfirmation)
}
