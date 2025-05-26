package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.repository.jpa.OrderItemJpaEntity
import com.cafe.payment.order.repository.jpa.OrderItemJpaRepository
import org.springframework.stereotype.Repository

interface OrderItemRepository {
    fun saveAll(orderItems: List<OrderItem>): List<OrderItem>

    fun findByOrderId(orderId: OrderId): List<OrderItem>
}

@Repository
class OrderItemRepositoryImpl(
    private val orderItemJpaRepository: OrderItemJpaRepository,
) : OrderItemRepository {
    override fun saveAll(orderItems: List<OrderItem>): List<OrderItem> {
        val entities = orderItems.map { OrderItemJpaEntity.fromDomain(it) }
        val savedEntities = orderItemJpaRepository.saveAll(entities)
        return savedEntities.map { it.toDomain() }
    }

    override fun findByOrderId(orderId: OrderId): List<OrderItem> {
        val entities = orderItemJpaRepository.findByOrderId(orderId.value)
        return entities.map { it.toDomain() }
    }
}
