package com.cafe.payment.library

enum class HttpStatusCode(val code: Int, val reasonPhrase: String) {
    // 2xx
    OK(200, "OK"),

    // 4xx
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    CONFLICT(409, "Conflict"),

    // 5xx
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
}
