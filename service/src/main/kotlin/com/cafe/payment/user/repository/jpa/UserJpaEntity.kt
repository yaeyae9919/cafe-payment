package com.cafe.payment.user.repository.jpa

import com.cafe.payment.user.domain.Gender
import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.repository.jpa.converter.EncryptedLocalDateConverter
import com.cafe.payment.user.repository.jpa.converter.EncryptedString
import com.cafe.payment.user.repository.jpa.converter.EncryptedStringConverter
import com.cafe.payment.user.repository.jpa.converter.GenderConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
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
    @Convert(converter = EncryptedStringConverter::class)
    @Column(name = "phone_number", nullable = false, length = 100, unique = true)
    val phoneNumber: EncryptedString,
    @Convert(converter = GenderConverter::class)
    @Column(nullable = false)
    val gender: Gender,
    @Convert(converter = EncryptedLocalDateConverter::class)
    @Column(name = "birth_date", nullable = false, length = 50)
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
            phoneNumber = this.phoneNumber.value,
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
                phoneNumber = EncryptedString(user.phoneNumber),
                gender = user.gender,
                birthDate = user.birthDate,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
            )
        }
    }
}
