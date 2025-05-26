package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderPayConfirmation
import com.cafe.payment.order.repository.jpa.OrderPayConfirmationJpaEntity
import com.cafe.payment.order.repository.jpa.OrderPayConfirmationJpaRepository
import org.springframework.stereotype.Repository

interface OrderPayConfirmationRepository {
    fun save(orderPayConfirmation: OrderPayConfirmation): OrderPayConfirmation

    fun findByOrderId(orderId: OrderId): OrderPayConfirmation?
}

@Repository
class OrderPayConfirmationRepositoryImpl(
    private val orderPayConfirmationJpaRepository: OrderPayConfirmationJpaRepository,
) : OrderPayConfirmationRepository {
    override fun save(orderPayConfirmation: OrderPayConfirmation): OrderPayConfirmation {
        val entity = OrderPayConfirmationJpaEntity.fromDomain(orderPayConfirmation)
        val savedEntity = orderPayConfirmationJpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findByOrderId(orderId: OrderId): OrderPayConfirmation? {
        return orderPayConfirmationJpaRepository.findByOrderId(orderId.value)?.toDomain()
    }
}
