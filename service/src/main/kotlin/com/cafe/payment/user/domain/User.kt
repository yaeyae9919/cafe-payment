package com.cafe.payment.user.domain

import com.cafe.payment.user.InvalidUserDataException
import com.cafe.payment.user.UserStatusException
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
    val withdrawnAt: LocalDateTime?,
) {
    val status: UserStatus =
        if (withdrawnAt == null) {
            UserStatus.ACTIVE
        } else {
            UserStatus.WITHDRAWN
        }

    val accountRevokableDate: LocalDate? =
        if (withdrawnAt == null) {
            null
        } else {
            val withdrawnDate = withdrawnAt.toLocalDate()
            withdrawnDate.plusDays(WITHDRAWAL_REVOKE_DAYS)
        }

    fun isActive(): Boolean = status.isActive()

    // 탈퇴 여부
    fun isWithdrawn(): Boolean = status.isWithdrawn()

    // 탈퇴
    fun withdraw(now: LocalDateTime = LocalDateTime.now()): User {
        if (isWithdrawn()) throw UserStatusException.alreadyWithdrawn()

        return modify(
            withdrawnAt = now,
            now = now,
        )
    }

    // 탈퇴 철회
    fun revokeWithdrawal(now: LocalDateTime = LocalDateTime.now()): User {
        if (isActive() || withdrawnAt == null) throw UserStatusException.notWithdrawn()

        if (cannotRevokeWithdrawal(now)) throw UserStatusException.withdrawalRevokePeriodExpired()

        return modify(
            withdrawnAt = null,
            now = now,
        )
    }

    // 탈퇴한 지 WITHDRAWAL_REVOKE_DAYS 이 지나면 탈퇴 철회할 수 없다.
    fun cannotRevokeWithdrawal(now: LocalDateTime): Boolean {
        val currentDate = now.toLocalDate()
        return currentDate.isAfter(accountRevokableDate)
    }

    private fun modify(
        withdrawnAt: LocalDateTime? = this.withdrawnAt,
        now: LocalDateTime = LocalDateTime.now(),
    ): User {
        return User(
            id = this.id,
            name = this.name,
            phoneNumber = this.phoneNumber,
            gender = this.gender,
            birthDate = this.birthDate,
            createdAt = this.createdAt,
            updatedAt = now,
            withdrawnAt = withdrawnAt,
        )
    }

    companion object {
        // 탈퇴 철회 가능 기간
        const val WITHDRAWAL_REVOKE_DAYS = 30L

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
                withdrawnAt = null,
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
