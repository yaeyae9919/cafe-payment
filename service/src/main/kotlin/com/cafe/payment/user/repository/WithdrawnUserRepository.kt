package com.cafe.payment.user.repository

import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.domain.WithdrawnUser
import com.cafe.payment.user.repository.jpa.WithdrawnUserJpaEntity
import com.cafe.payment.user.repository.jpa.WithdrawnUserJpaRepository
import org.springframework.stereotype.Repository

interface WithdrawnUserRepository {
    fun findById(userId: UserId): WithdrawnUser?

    fun deleteById(userId: UserId)

    fun save(withdrawnUser: WithdrawnUser): WithdrawnUser
}

@Repository
class WithdrawnUserRepositoryImpl(
    private val withdrawnUserJpaRepository: WithdrawnUserJpaRepository,
) : WithdrawnUserRepository {
    override fun save(withdrawnUser: WithdrawnUser): WithdrawnUser {
        val entity = WithdrawnUserJpaEntity.fromDomain(withdrawnUser)
        val savedEntity = withdrawnUserJpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(userId: UserId): WithdrawnUser? {
        return withdrawnUserJpaRepository.findByUserId(userId.value)?.toDomain()
    }

    override fun deleteById(userId: UserId) {
        withdrawnUserJpaRepository.deleteByUserId(userId.value)
    }
}
