package com.cafe.payment.user.application

import com.cafe.payment.user.domain.Gender
import com.cafe.payment.user.domain.UserId
import java.time.LocalDate

interface UserService {
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
