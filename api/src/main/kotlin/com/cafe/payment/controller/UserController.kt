package com.cafe.payment.controller

import com.cafe.payment.auth.UserContext
import com.cafe.payment.user.application.UserService
import com.cafe.payment.user.domain.Gender
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@Tag(name = "회원 관리")
@RestController
@RequestMapping("/api")
class UserController(
    private val userService: UserService,
) {
    @Operation(
        summary = "회원 가입",
        description = "동일한 전화번호로 이중 가입할 수 없습니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원 가입 성공",
                content = [Content(schema = Schema(implementation = UserIdPresentation::class))],
            ),
        ],
    )
    @PostMapping("/public/user/register")
    fun register(
        @RequestBody form: RegisterUserForm,
    ): UserIdPresentation {
        val userId = userService.register(form.toCommand())
        return UserIdPresentation(userId.toString())
    }

    @Operation(
        summary = "회원 탈퇴",
        description = "탈퇴 후 30일 이내에는 탈퇴 철회가 가능합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "회원 탈퇴 성공",
                content = [Content(schema = Schema(implementation = UserIdPresentation::class))],
            ),
        ],
    )
    @DeleteMapping("/user/withdraw")
    fun withdraw(): UserIdPresentation {
        val userId = UserContext.getCurrentUserId()
        val withDrawnUserId = userService.withdraw(userId)

        return UserIdPresentation(withDrawnUserId.toString())
    }

    @Operation(
        summary = "회원 탈퇴 철회",
        description = "탈퇴를 철회합니다. 탈퇴 후 30일 이내에만 가능합니다.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "탈퇴 철회 성공",
                content = [Content(schema = Schema(implementation = UserIdPresentation::class))],
            ),
        ],
    )
    @PostMapping("/user/revoke-withdrawal")
    fun revokeWithdrawal(): UserIdPresentation {
        val userId = UserContext.getCurrentUserId()
        val revokedUserId = userService.revokeWithdrawal(userId)

        return UserIdPresentation(revokedUserId.toString())
    }
}

@Schema(description = "사용자 ID 응답")
data class UserIdPresentation(
    @Schema(description = "사용자 ID", example = "1")
    val userId: String,
)

@Schema(description = "회원 가입 요청")
data class RegisterUserForm(
    @Schema(description = "이름", example = "홍길동")
    val name: String,
    @Schema(description = "전화번호", example = "010-1234-5678")
    val phoneNumber: String,
    @Schema(description = "성별", example = "MALE", allowableValues = ["MALE", "FEMALE"])
    val gender: String,
    @Schema(description = "생년월일", example = "1990-01-01")
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
