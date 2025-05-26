package com.cafe.payment.user.repository

import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.domain.WithdrawnUser
import org.springframework.stereotype.Repository

interface WithdrawnUserRepository {
    fun findById(userId: UserId): WithdrawnUser?

    fun deleteById(userId: UserId)

    fun save(withdrawnUser: WithdrawnUser): WithdrawnUser
}

@Repository
class InMemoryWithdrawnUserRepository : WithdrawnUserRepository {
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
}
