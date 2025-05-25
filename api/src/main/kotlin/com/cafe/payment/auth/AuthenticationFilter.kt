package com.cafe.payment.auth

import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.repository.UserRepository
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Order(1)
class AuthenticationFilter(
    private val userRepository: UserRepository,
) : Filter {
    companion object {
        private const val USER_ID_HEADER = "x-user-id"
        private val PUBLIC_PATHS =
            setOf(
                "/api/public/",
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
        val user = userRepository.findById(userId)
        if (user == null) {
            throw UserAuthenticationException.userNotFound()
        }

        // 탈퇴 케이스 핸들링
        val now = LocalDateTime.now()
        val userIsWithDrawn = user.isWithdrawn()

        // 탈퇴 철회 불가능이라면 삭제된 사용자로서 취급
        if (userIsWithDrawn && user.cannotRevokeWithdrawal(now)) {
            throw UserAuthenticationException.userNotFound()
        }

        // 탈퇴한 사용자는 탈퇴 철회 API만 호출 가능
        if (userIsWithDrawn && !isWithdrawalRevokePath(requestPath)) {
            throw UserAuthenticationException.withdrawnUser()
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
