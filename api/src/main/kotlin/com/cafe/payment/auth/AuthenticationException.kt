package com.cafe.payment.auth

import com.cafe.payment.library.HttpStatusCode
import com.cafe.payment.library.exception.CustomException

sealed class AuthenticationException(
    statusCode: HttpStatusCode,
    errorCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(statusCode, errorCode, message, cause)

class UserNotAuthenticatedException(
    errorCode: String,
    message: String,
) : AuthenticationException(HttpStatusCode.UNAUTHORIZED, errorCode, message, null) {
    companion object {
        fun userNotAuthenticated(): UserNotAuthenticatedException {
            return UserNotAuthenticatedException(
                errorCode = "AUTH_001",
                message = "사용자 인증에 실패했어요.",
            )
        }
    }
}

// 헤더 검증 관련 예외
class InvalidUserHeaderException(
    errorCode: String,
    message: String,
) : AuthenticationException(HttpStatusCode.BAD_REQUEST, errorCode, message, null) {
    companion object {
        fun invalidUserHeader(): InvalidUserHeaderException {
            return InvalidUserHeaderException(
                errorCode = "AUTH_002",
                message = "유효하지 않은 사용자 헤더에요.",
            )
        }
    }
}

// 사용자 조회 관련 예외
class UserAuthenticationException(
    errorCode: String,
    message: String,
) : AuthenticationException(HttpStatusCode.UNAUTHORIZED, errorCode, message, null) {
    companion object {
        fun userNotFound(): UserAuthenticationException {
            return UserAuthenticationException(
                errorCode = "AUTH_003",
                message = "존재하지 않는 사용자에요.",
            )
        }

        fun withdrawnUser(): UserAuthenticationException {
            return UserAuthenticationException(
                errorCode = "AUTH_004",
                message = "탈퇴한 사용자에요. 탈퇴 철회 후 이용할 수 있어요.",
            )
        }
    }
} 
