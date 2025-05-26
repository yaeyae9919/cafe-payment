package com.cafe.payment.user.repository.jpa

import com.cafe.payment.user.domain.Gender
import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.domain.WithdrawnUser
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "withdrawn_users")
class WithdrawnUserJpaEntity(
    @Id
    val userId: Long,
    @Column(nullable = false, length = 100)
    val name: String,
    @Column(name = "phone_number", nullable = false, length = 30, unique = true)
    val phoneNumber: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val gender: Gender,
    @Column(name = "birth_date", nullable = false)
    val birthDate: LocalDate,
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime,
    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime,
    @Column(name = "withdrawn_at", nullable = false)
    val withdrawnAt: LocalDateTime,
) {
    fun toDomain(): WithdrawnUser {
        val user =
            User(
                id = UserId(this.userId),
                name = this.name,
                phoneNumber = this.phoneNumber,
                gender = this.gender,
                birthDate = this.birthDate,
                createdAt = this.createdAt,
                updatedAt = this.updatedAt,
            )
        return WithdrawnUser(
            user = user,
            withdrawnAt = this.withdrawnAt,
        )
    }

    companion object {
        fun fromDomain(withdrawnUser: WithdrawnUser): WithdrawnUserJpaEntity {
            return WithdrawnUserJpaEntity(
                userId = withdrawnUser.userId.value,
                name = withdrawnUser.user.name,
                phoneNumber = withdrawnUser.user.phoneNumber,
                gender = withdrawnUser.user.gender,
                birthDate = withdrawnUser.user.birthDate,
                createdAt = withdrawnUser.user.createdAt,
                updatedAt = withdrawnUser.user.updatedAt,
                withdrawnAt = withdrawnUser.withdrawnAt,
            )
        }
    }
}
