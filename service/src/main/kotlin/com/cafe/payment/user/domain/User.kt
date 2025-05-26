package com.cafe.payment.user.domain

import com.cafe.payment.user.InvalidUserDataException
import java.time.LocalDate
import java.time.LocalDateTime

@JvmInline
value class UserId(val value: Long) {
    override fun toString(): String {
        return value.toString()
    }
}

class User internal constructor(
    val id: UserId,
    val name: String,
    val phoneNumber: String,
    val gender: Gender,
    val birthDate: LocalDate,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun create(
            id: UserId,
            name: String,
            phoneNumber: String,
            gender: Gender,
            birthDate: LocalDate,
            now: LocalDateTime = LocalDateTime.now(),
        ): User {
            if (name.isBlank()) throw InvalidUserDataException.invalidName()
            if (phoneNumber.isBlank()) throw InvalidUserDataException.invalidPhoneNumber()
            if (!phoneNumber.all { it.isDigit() }) throw InvalidUserDataException.invalidPhoneNumberFormat()

            return User(
                id = id,
                name = name,
                phoneNumber = phoneNumber,
                gender = gender,
                birthDate = birthDate,
                createdAt = now,
                updatedAt = now,
            )
        }
    }
}

enum class Gender {
    MALE,
    FEMALE,
}
