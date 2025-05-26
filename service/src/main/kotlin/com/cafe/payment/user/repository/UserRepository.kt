package com.cafe.payment.user.repository

import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import org.springframework.stereotype.Repository

interface UserRepository {
    fun save(user: User): User

    fun findById(userId: UserId): User?

    fun deleteById(userId: UserId)
}

@Repository
class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<UserId, User>()

    override fun save(user: User): User {
        users[user.id] = user
        return user
    }

    override fun findById(userId: UserId): User? {
        return users[userId]
    }

    override fun deleteById(userId: UserId) {
        users.remove(userId)
    }
}
