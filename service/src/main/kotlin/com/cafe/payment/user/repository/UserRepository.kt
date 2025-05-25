package com.cafe.payment.user.repository

import com.cafe.payment.user.domain.User
import org.springframework.stereotype.Repository

interface UserRepository {
    fun save(user: User): User
}

@Repository
class NoOpUserRepository : UserRepository {
    override fun save(user: User): User {
        return user
    }
}
