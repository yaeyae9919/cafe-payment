package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.OrderPayConfirmation
import org.springframework.stereotype.Repository

interface OrderPayConfirmationRepository {
    fun save(orderPayConfirmation: OrderPayConfirmation): OrderPayConfirmation
}

@Repository
class NoOpOrderPayConfirmationRepository : OrderPayConfirmationRepository {
    override fun save(orderPayConfirmation: OrderPayConfirmation): OrderPayConfirmation {
        return orderPayConfirmation
    }
}
