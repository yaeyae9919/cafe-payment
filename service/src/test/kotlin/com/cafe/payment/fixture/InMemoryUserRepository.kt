package com.cafe.payment.fixture

import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.repository.UserRepository

class InMemoryUserRepository : UserRepository {
    private val users = mutableMapOf<UserId, User>()

    override fun save(user: User): User {
        users[user.id] = user
        return user
    }

    override fun findById(userId: UserId): User? {
        return users[userId]
    }

    override fun findByPhoneNumber(phoneNumber: String): User? {
        return users.values.find { it.phoneNumber == phoneNumber }
    }

    override fun deleteById(userId: UserId) {
        users.remove(userId)
    }

    fun clear() {
        users.clear()
    }
}
