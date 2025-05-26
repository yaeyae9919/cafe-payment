package com.cafe.payment.order.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemJpaRepository : JpaRepository<OrderItemJpaEntity, Long> {
    // saveAll 사용
    fun findByOrderId(orderId: Long): List<OrderItemJpaEntity>
}
