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

class User private constructor(
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

enum class UserStatus {
    // 정상
    ACTIVE,

    // 탈퇴
    WITHDRAWN,

    // 휴면상태, 제재 등 추가 가능
    ;

    fun isActive(): Boolean = this == ACTIVE

    fun isWithdrawn(): Boolean = this == WITHDRAWN
}
