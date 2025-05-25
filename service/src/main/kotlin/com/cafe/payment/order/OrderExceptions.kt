package com.cafe.payment.order

// 주문 상태 관련 예외
class OrderConfirmationStatusException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    companion object {
        fun alreadyCanceled() = OrderConfirmationStatusException("주문이 이미 취소되었어요.")

        fun alreadyPaid() = OrderConfirmationStatusException("주문이 이미 완료되었어요.")

        fun notPaid() = OrderConfirmationStatusException("아직 주문이 완료되지 않았어요.")
    }
}

// 주문 데이터 유효성 관련 예외
class InvalidOrderItemException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    companion object {
        fun invalidQuantity() = InvalidOrderItemException("주문 수량은 1개 이상이어야 합니다.")

        fun invalidOrderItems() = InvalidOrderItemException("주문 상품이 비어있습니다.")
    }
}
