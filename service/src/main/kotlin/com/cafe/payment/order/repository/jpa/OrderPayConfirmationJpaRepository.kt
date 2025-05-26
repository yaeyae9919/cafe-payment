package com.cafe.payment.order.repository.jpa

import org.springframework.data.repository.Repository

interface OrderPayConfirmationJpaRepository : Repository<OrderPayConfirmationJpaEntity, Long> {
    fun save(entity: OrderPayConfirmationJpaEntity): OrderPayConfirmationJpaEntity

    fun findByOrderId(orderId: Long): OrderPayConfirmationJpaEntity?
}
