package com.cafe.payment.order.repository

import com.cafe.payment.order.domain.OrderPayHistory
import com.cafe.payment.order.repository.jpa.OrderPayHistoryJpaEntity
import com.cafe.payment.order.repository.jpa.OrderPayHistoryJpaRepository
import org.springframework.stereotype.Repository

interface OrderPayHistoryRepository {
    fun record(history: OrderPayHistory)
}

@Repository
class OrderPayHistoryRepositoryImpl(
    private val orderPayHistoryJpaRepository: OrderPayHistoryJpaRepository,
) : OrderPayHistoryRepository {
    override fun record(history: OrderPayHistory) {
        val entity = OrderPayHistoryJpaEntity.fromDomain(history)
        orderPayHistoryJpaRepository.save(entity)
    }
}
