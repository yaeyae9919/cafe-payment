package com.cafe.payment.library.exception

import com.cafe.payment.library.HttpStatusCode

// 커스텀 예외 클래스
open class CustomException(
    val statusCode: HttpStatusCode = HttpStatusCode.INTERNAL_SERVER_ERROR,
    val errorCode: String,
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
