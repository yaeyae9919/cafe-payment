package com.cafe.payment.user.application

import com.cafe.payment.user.domain.Gender
import com.cafe.payment.user.domain.UserId
import java.time.LocalDate
import java.time.LocalDateTime

interface UserService {
    // 활성 상태 유저인지 확인
    fun isActive(userId: UserId): Boolean

    // 탈퇴 철회 가능인지 확인
    fun isRevokableWithdrawn(
        userId: UserId,
        now: LocalDateTime,
    ): Boolean

    fun register(command: RegisterCommand): UserId

    fun withdraw(userId: UserId): UserId

    fun revokeWithdrawal(userId: UserId): UserId

    data class RegisterCommand(
        val name: String,
        val phoneNumber: String,
        val gender: Gender,
        val birthDate: LocalDate,
    )
}
