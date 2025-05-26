package com.cafe.payment.order.repository.jpa

import org.springframework.data.repository.Repository

interface OrderJpaRepository : Repository<OrderJpaEntity, Long> {
    fun save(entity: OrderJpaEntity): OrderJpaEntity

    fun findById(id: Long): OrderJpaEntity?
}
