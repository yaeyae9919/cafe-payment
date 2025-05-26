package com.cafe.payment.user.repository.jpa

import org.springframework.data.repository.Repository

interface UserJpaRepository : Repository<UserJpaEntity, Long> {
    fun findById(id: Long): UserJpaEntity?

    fun findByPhoneNumber(phoneNumber: String): UserJpaEntity?

    fun save(entity: UserJpaEntity): UserJpaEntity

    fun deleteById(id: Long)
}
