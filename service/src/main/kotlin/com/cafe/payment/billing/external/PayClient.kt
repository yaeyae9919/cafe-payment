package com.cafe.payment.billing.external

import com.cafe.payment.billing.domain.PayId
import com.cafe.payment.billing.domain.PayTransactionId
import com.cafe.payment.library.HttpStatusCode
import com.cafe.payment.library.exception.CustomException
import com.cafe.payment.order.domain.OrderId
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * (외부) 결제 서버 호출 인터페이스
 * 중복 결제와 같은 케이스는 반드시 막아준다고 가정합니다.
 */
interface PayClient {
    fun obtainPayId(orderId: OrderId): PayId

    // 결제 요청
    fun pay(
        payId: PayId,
        totalAmount: BigDecimal,
    ): Result<PayResult>

    // 결제 취소 요청
    fun refund(
        payId: PayId,
        totalAmount: BigDecimal,
    ): Result<PayResult>
}

// 결제 성공 결과
data class PayResult(
    val payId: PayId,
    val transactionId: PayTransactionId,
    val amount: BigDecimal,
    val transactionAt: LocalDateTime,
)

sealed class PayFailure(
    statusCode: HttpStatusCode,
    errorCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(statusCode, errorCode, message, cause) {
    // 결제 서버 실패 (결제 서버 내 실패로 아예 결제/결제 취소되지 않음)
    class InternalServerError(
        cause: Throwable? = null,
    ) : PayFailure(
            statusCode = HttpStatusCode.INTERNAL_SERVER_ERROR,
            errorCode = "BILLING_FAILURE_001",
            message = "결제 서버에서 오류가 발생했어요.",
            cause = cause,
        )

    // 타임아웃 실패 (결제 되었는지, 결제되지 않았는지 알 수 없음. 결제 취소도 마찬가지)
    class TimeoutError(
        cause: Throwable? = null,
    ) : PayFailure(
            statusCode = HttpStatusCode.GATEWAY_TIMEOUT,
            errorCode = "BILLING_FAILURE_002",
            message = "결제 서버 연결이 지연되었어요.",
            cause = cause,
        )

    // 중복 결제로 인한 실패
    class AlreadyPaidError(
        cause: Throwable? = null,
    ) : PayFailure(
            statusCode = HttpStatusCode.BAD_REQUEST,
            errorCode = "BILLING_FAILURE_003",
            message = "이미 결제되었어요.",
            cause = cause,
        )
}
