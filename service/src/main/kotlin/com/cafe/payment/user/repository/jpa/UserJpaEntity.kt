package com.cafe.payment.user.repository.jpa

import com.cafe.payment.user.domain.Gender
import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class UserJpaEntity(
    @Id
    val id: Long,
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
) {
    fun toDomain(): User {
        return User(
            id = UserId(this.id),
            name = this.name,
            phoneNumber = this.phoneNumber,
            gender = this.gender,
            birthDate = this.birthDate,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
        )
    }

    companion object {
        fun fromDomain(user: User): UserJpaEntity {
            return UserJpaEntity(
                id = user.id.value,
                name = user.name,
                phoneNumber = user.phoneNumber,
                gender = user.gender,
                birthDate = user.birthDate,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
            )
        }
    }
}
