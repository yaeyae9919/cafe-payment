package com.cafe.payment.user.repository.jpa

import org.springframework.data.repository.Repository

interface WithdrawnUserJpaRepository : Repository<WithdrawnUserJpaEntity, Long> {
    fun findByUserId(userId: Long): WithdrawnUserJpaEntity?

    fun save(entity: WithdrawnUserJpaEntity): WithdrawnUserJpaEntity

    fun deleteByUserId(userId: Long)
}
