package com.cafe.payment.order

import com.cafe.payment.library.HttpStatusCode
import com.cafe.payment.library.exception.CustomException

sealed class OrderException(
    statusCode: HttpStatusCode,
    errorCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(statusCode, errorCode, message, cause)

// 주문 상태 관련 예외
class OrderConfirmationStatusException(
    errorCode: String,
    message: String,
) : OrderException(HttpStatusCode.BAD_REQUEST, errorCode, message, null) {
    companion object {
        fun alreadyCanceled(): OrderConfirmationStatusException {
            return OrderConfirmationStatusException(
                errorCode = "ORDER_STATUS_001",
                message = "주문이 이미 취소되었어요.",
            )
        }

        fun alreadyPaid(): OrderConfirmationStatusException {
            return OrderConfirmationStatusException(
                errorCode = "ORDER_STATUS_002",
                message = "주문이 이미 완료되었어요.",
            )
        }

        fun notPaid(): OrderConfirmationStatusException {
            return OrderConfirmationStatusException(
                errorCode = "ORDER_STATUS_003",
                message = "아직 주문이 완료되지 않았어요.",
            )
        }
    }
}

// 주문 데이터 유효성 관련 예외
class InvalidOrderItemException(
    errorCode: String,
    message: String,
) : OrderException(HttpStatusCode.BAD_REQUEST, errorCode, message, null) {
    companion object {
        fun invalidQuantity(): InvalidOrderItemException {
            return InvalidOrderItemException(
                errorCode = "ORDER_ITEM_001",
                message = "주문 수량은 1개 이상이어야 합니다.",
            )
        }

        fun invalidOrderItems(): InvalidOrderItemException {
            return InvalidOrderItemException(
                errorCode = "ORDER_ITEM_002",
                message = "주문 상품이 비어있습니다.",
            )
        }
    }
}
