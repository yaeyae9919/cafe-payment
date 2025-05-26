package com.cafe.payment.user.domain

import com.cafe.payment.user.UserStatusException
import java.time.LocalDate
import java.time.LocalDateTime

class WithdrawnUser(
    val user: User,
    val withdrawnAt: LocalDateTime,
) {
    val userId = user.id
    val accountRevokableDate: LocalDate = withdrawnAt.toLocalDate().plusDays(WITHDRAWAL_REVOKE_DAYS)

    // 탈퇴 철회 가능 여부 판단
    fun cannotRevokable(now: LocalDateTime): Boolean {
        val currentDate = now.toLocalDate()
        return currentDate.isAfter(accountRevokableDate)
    }

    fun toRevokedUser(now: LocalDateTime): User {
        if (cannotRevokable(now)) throw UserStatusException.withdrawalRevokePeriodExpired()
        return user
    }

    companion object {
        // 탈퇴 철회 가능 기간
        const val WITHDRAWAL_REVOKE_DAYS = 30L

        fun create(
            user: User,
            now: LocalDateTime,
        ): WithdrawnUser =
            WithdrawnUser(
                user = user,
                withdrawnAt = now,
            )
    }
}
