package com.cafe.payment.controller

import com.cafe.payment.auth.UserContext
import com.cafe.payment.user.application.UserService
import com.cafe.payment.user.domain.Gender
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api")
class UserController(
    private val userService: UserService,
) {
    // 회원 가입
    @PostMapping("/public/user/register")
    fun register(
        @RequestBody form: RegisterUserForm,
    ): UserIdPresentation {
        val userId = userService.register(form.toCommand())
        return UserIdPresentation(userId.value)
    }

    // 회원 탈퇴
    @DeleteMapping("/user/withdraw")
    fun withdraw(): UserIdPresentation {
        val userId = UserContext.getCurrentUserId()
        val withDrawnUserId = userService.withdraw(userId)

        return UserIdPresentation(withDrawnUserId.value)
    }

    // 회원 탈퇴 철회
    @PostMapping("/user/revoke-withdrawal")
    fun revokeWithdrawal(): UserIdPresentation {
        val userId = UserContext.getCurrentUserId()
        val revokedUserId = userService.revokeWithdrawal(userId)

        return UserIdPresentation(revokedUserId.value)
    }
}

data class UserIdPresentation(
    val userId: Long,
)

data class RegisterUserForm(
    val name: String,
    val phoneNumber: String,
    val gender: String,
    val birthDate: LocalDate,
) {
    fun toCommand() =
        UserService.RegisterCommand(
            name = name,
            phoneNumber = phoneNumber,
            gender = Gender.valueOf(gender),
            birthDate = birthDate,
        )
}
