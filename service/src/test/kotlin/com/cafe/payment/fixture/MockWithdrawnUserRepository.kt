package com.cafe.payment.fixture

import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.domain.WithdrawnUser
import com.cafe.payment.user.repository.WithdrawnUserRepository

class MockWithdrawnUserRepository : WithdrawnUserRepository {
    private val users = mutableMapOf<UserId, WithdrawnUser>()

    override fun save(withdrawnUser: WithdrawnUser): WithdrawnUser {
        users[withdrawnUser.userId] = withdrawnUser
        return withdrawnUser
    }

    override fun deleteById(userId: UserId) {
        users.remove(userId)
    }

    override fun findById(userId: UserId): WithdrawnUser? {
        return users[userId]
    }

    fun clear() {
        users.clear()
    }
}
