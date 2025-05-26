package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.repository.jpa.OrderJpaEntity
import com.cafe.payment.order.repository.jpa.OrderJpaRepository
import org.springframework.stereotype.Repository

interface OrderRepository {
    fun findById(orderId: OrderId): Order?

    fun save(order: Order): Order
}

@Repository
class OrderRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository,
    private val orderItemRepository: OrderItemRepository,
) : OrderRepository {
    override fun findById(orderId: OrderId): Order? {
        val entity = orderJpaRepository.findById(orderId.value) ?: return null

        // OrderItem들을 조회하여 itemIds 구성
        val orderItems = orderItemRepository.findByOrderId(orderId)
        val itemIds = orderItems.map { it.id }

        return entity.toDomain(itemIds)
    }

    override fun save(order: Order): Order {
        val entity = OrderJpaEntity.fromDomain(order)
        val savedEntity = orderJpaRepository.save(entity)
        return savedEntity.toDomain(order.itemIds)
    }
}
