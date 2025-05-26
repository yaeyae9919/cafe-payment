package com.cafe.payment.user.repository

import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.repository.jpa.UserJpaEntity
import com.cafe.payment.user.repository.jpa.UserJpaRepository
import org.springframework.stereotype.Repository

interface UserRepository {
    fun save(user: User): User

    fun findById(userId: UserId): User?

    fun deleteById(userId: UserId)
}

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun save(user: User): User {
        val entity = UserJpaEntity.fromDomain(user)
        val savedEntity = userJpaRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(userId: UserId): User? {
        return userJpaRepository.findById(userId.value)?.toDomain()
    }

    override fun deleteById(userId: UserId) {
        userJpaRepository.deleteById(userId.value)
    }
}
