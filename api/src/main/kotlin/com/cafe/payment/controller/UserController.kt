package com.cafe.payment.controller

import com.cafe.payment.user.application.UserService
import com.cafe.payment.user.domain.Gender
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
    @PostMapping("/user/register")
    fun register(
        @RequestBody form: RegisterUserForm,
    ): UserIdPresentation {
        val userId = userService.register(form.toCommand())
        return UserIdPresentation(userId.value)
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
