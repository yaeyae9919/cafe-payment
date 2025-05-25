package com.cafe.payment.user

import com.cafe.payment.library.HttpStatusCode
import com.cafe.payment.library.exception.CustomException

sealed class UserException(
    statusCode: HttpStatusCode,
    errorCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(statusCode, errorCode, message, cause)

// 입력 데이터 검증 실패
class InvalidUserDataException(
    errorCode: String,
    message: String,
) : UserException(HttpStatusCode.BAD_REQUEST, errorCode, message, null) {
    companion object {
        fun invalidName(): InvalidUserDataException {
            return InvalidUserDataException(
                errorCode = "USER_001",
                message = "이름을 입력해주세요.",
            )
        }

        fun invalidPhoneNumber(): InvalidUserDataException {
            return InvalidUserDataException(
                errorCode = "USER_002",
                message = "전화번호를 입력해주세요.",
            )
        }

        fun invalidPhoneNumberFormat(): InvalidUserDataException {
            return InvalidUserDataException(
                errorCode = "USER_003",
                message = "전화번호는 숫자만 입력해주세요.",
            )
        }
    }
}

// 상태 관련 예외
class UserStatusException(
    errorCode: String,
    message: String,
) : UserException(HttpStatusCode.BAD_REQUEST, errorCode, message, null) {
    companion object {
        fun alreadyWithdrawn(): UserStatusException {
            return UserStatusException(
                errorCode = "USER_STATUS_001",
                message = "이미 탈퇴했어요.",
            )
        }

        fun notWithdrawn(): UserStatusException {
            return UserStatusException(
                errorCode = "USER_STATUS_002",
                message = "탈퇴하지 않았어요.",
            )
        }

        fun withdrawalRevokePeriodExpired(): UserStatusException {
            return UserStatusException(
                errorCode = "USER_STATUS_003",
                message = "탈퇴 철회 가능 기간이 지났어요.",
            )
        }
    }
}

class UserNotFoundException(
    errorCode: String,
    message: String,
) : UserException(HttpStatusCode.NOT_FOUND, errorCode, message, null) {
    companion object {
        fun notFound(): UserNotFoundException {
            return UserNotFoundException(
                errorCode = "USER_NOT_FOUND_001",
                message = "존재하지 않는 사용자에요.",
            )
        }
    }
}
