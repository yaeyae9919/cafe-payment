package com.cafe.payment.order.repository.jpa

import org.springframework.data.repository.Repository

interface OrderPayHistoryJpaRepository : Repository<OrderPayHistoryJpaEntity, Long> {
    fun save(entity: OrderPayHistoryJpaEntity): OrderPayHistoryJpaEntity
} 
