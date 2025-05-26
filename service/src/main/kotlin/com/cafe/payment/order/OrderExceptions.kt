package com.cafe.payment.order

import com.cafe.payment.library.HttpStatusCode
import com.cafe.payment.library.exception.CustomException
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderStatus
import com.cafe.payment.user.domain.UserId

sealed class OrderException(
    statusCode: HttpStatusCode,
    errorCode: String,
    message: String,
    cause: Throwable? = null,
) : CustomException(statusCode, errorCode, message, cause)

// 주문 상태 관련 예외
class OrderPayConfirmationStatusException(
    errorCode: String,
    message: String,
) : OrderException(HttpStatusCode.BAD_REQUEST, errorCode, message, null) {
    companion object {
        fun alreadyCanceled(): OrderPayConfirmationStatusException {
            return OrderPayConfirmationStatusException(
                errorCode = "ORDER_STATUS_001",
                message = "주문이 이미 취소되었어요.",
            )
        }

        fun alreadyPaid(): OrderPayConfirmationStatusException {
            return OrderPayConfirmationStatusException(
                errorCode = "ORDER_STATUS_002",
                message = "주문이 이미 완료되었어요.",
            )
        }

        fun notPaid(): OrderPayConfirmationStatusException {
            return OrderPayConfirmationStatusException(
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

        fun orderItemIsZero(): InvalidOrderItemException {
            return InvalidOrderItemException(
                errorCode = "ORDER_ITEM_002",
                message = "주문 상품이 비어있습니다.",
            )
        }
    }
}

class OrderStatusTransitionException(
    errorCode: String,
    message: String,
) : OrderException(HttpStatusCode.BAD_REQUEST, errorCode, message, null) {
    companion object {
        fun invalidOrderStatusTransition(
            from: OrderStatus,
            to: OrderStatus,
        ): OrderStatusTransitionException {
            return OrderStatusTransitionException(
                errorCode = "ORDER_STATUS_TRANSITION_001",
                message = "주문을 $from 상태에서 $to 상태로 변경할 수 없어요.",
            )
        }
    }
}

class OrderNotFoundException(
    errorCode: String,
    message: String,
) : OrderException(HttpStatusCode.NOT_FOUND, errorCode, message, null) {
    companion object {
        fun notFoundOrder(orderId: OrderId): OrderNotFoundException {
            return OrderNotFoundException(
                errorCode = "ORDER_NOT_FOUND_001",
                message = "존재하지 않는 주문 정보에요. ($orderId)",
            )
        }

        fun notFoundConfirmation(orderId: OrderId): OrderNotFoundException {
            return OrderNotFoundException(
                errorCode = "ORDER_NOT_FOUND_002",
                message = "주문 결제 내역을 찾을 수 없어요. ($orderId)",
            )
        }
    }
}

class OrderPayException(
    statusCode: HttpStatusCode,
    errorCode: String,
    message: String,
) : OrderException(statusCode, errorCode, message, null) {
    companion object {
        fun isNotBuyer(requesterId: UserId): OrderPayException {
            return OrderPayException(
                statusCode = HttpStatusCode.FORBIDDEN,
                errorCode = "ORDER_PAY_001",
                message = "내가 주문한게 아니에요. ($requesterId)",
            )
        }

        fun cancleOnlyWhenOrderCompleted(): OrderPayException {
            return OrderPayException(
                statusCode = HttpStatusCode.BAD_REQUEST,
                errorCode = "ORDER_PAY_002",
                message = "완료된 주문만 취소할 수 있어요.",
            )
        }

        fun cancleOnlyWhenOrderPaid(): OrderPayException {
            return OrderPayException(
                statusCode = HttpStatusCode.BAD_REQUEST,
                errorCode = "ORDER_PAY_003",
                message = "결제 완료된 주문만 취소할 수 있어요.",
            )
        }
    }
}
