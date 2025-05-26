package com.cafe.payment.auth

import com.cafe.payment.user.application.UserService
import com.cafe.payment.user.domain.UserId
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * 해당 서버는 인증 서버를 통해 인증된 사용자들만 접근 가능하다고 가정합니다.
 */
@Component
@Order(1)
class AuthenticationFilter(
    private val userService: UserService,
) : Filter {
    companion object {
        private const val USER_ID_HEADER = "x-user-id"
        private val PUBLIC_PATHS =
            setOf(
                "/api/public/",
                "/h2-console",
            )
        private val REVOKE_WITHDRAWAL_PATH =
            setOf(
                "/api/user/revoke-withdrawal",
            )
    }

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        val httpRequest = request as HttpServletRequest

        val requestPath = httpRequest.requestURI

        // public API는 인증 생략
        if (isPublicPath(requestPath)) {
            chain.doFilter(request, response)
            return
        }

        // x-user-id 헤더 검증
        val userIdHeader = httpRequest.getHeader(USER_ID_HEADER)
        if (userIdHeader.isNullOrBlank()) {
            throw InvalidUserHeaderException.invalidUserHeader()
        }

        val userId =
            try {
                UserId(userIdHeader.toLong())
            } catch (e: NumberFormatException) {
                throw InvalidUserHeaderException.invalidUserHeader()
            }
        // 사용자 존재 여부 및 활성 상태 확인
        val isActiveUser = userService.isActive(userId)
        val isRevokableWithdrawn = userService.isRevokableWithdrawn(userId, LocalDateTime.now())

        when {
            // 활성유저의 경우 패스
            isActiveUser -> Unit
            // 탈퇴 & 탈퇴 철회 가능 사용자라면 탈퇴 철회 API 호출 가능
            isRevokableWithdrawn && isWithdrawalRevokePath(requestPath) -> Unit
            // 탈퇴 & 탈퇴 철회 가능 사용자라면 탈퇴 철회 API"만" 호출 가능
            isRevokableWithdrawn && isWithdrawalRevokePath(requestPath) -> {
                throw UserAuthenticationException.withdrawnUser()
            }
            // 탈퇴 & 탈퇴 철회 불가 사용자라면 삭제된 사용자로서 취급
            else -> throw UserAuthenticationException.userNotFound()
        }

        // UserContext에 사용자 ID 설정
        UserContext.setUserId(userId)

        chain.doFilter(request, response)
    }

    private fun isPublicPath(path: String): Boolean {
        return PUBLIC_PATHS.any { publicPath -> path.startsWith(publicPath) }
    }

    private fun isWithdrawalRevokePath(path: String): Boolean {
        return REVOKE_WITHDRAWAL_PATH.contains(path)
    }
}
