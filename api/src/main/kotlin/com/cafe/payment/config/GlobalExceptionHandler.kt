package com.cafe.payment.config

import com.cafe.payment.library.exception.CustomException
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(
        ex: CustomException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val errorResponse =
            ErrorResponse(
                status = ex.statusCode.code,
                error = ex.statusCode.reasonPhrase,
                message = ex.message ?: "알 수 없는 오류가 발생했습니다.",
                path = request.getDescription(false).removePrefix("uri="),
                timestamp = LocalDateTime.now(),
            )

        return ResponseEntity
            .status(ex.statusCode.code)
            .body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleSystemException(
        ex: Exception,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "시스템 오류 발생: ${ex.message}" }
        val errorResponse =
            ErrorResponse(
                status = 500,
                error = "Internal Server Error",
                message = "시스템 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
                path = request.getDescription(false).removePrefix("uri="),
                timestamp = LocalDateTime.now(),
            )

        return ResponseEntity
            .status(500)
            .body(errorResponse)
    }

    data class ErrorResponse(
        val status: Int,
        val error: String,
        val message: String,
        val path: String,
        val timestamp: LocalDateTime,
    )
}
