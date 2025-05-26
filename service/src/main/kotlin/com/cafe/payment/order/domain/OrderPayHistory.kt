package com.cafe.payment.order.domain

import com.cafe.payment.billing.external.PayResult

/**
 * 주문 - 결제 상세 내역 히스토리
 * 주문 / 주문 취소 트랜잭션이 실행될 때의 결과 내역을 모두 저장한다.
 * payResult(결제 결과)가 제공하는 다양한 결제 관련 맥락들을 저장하는 역할
 */
data class OrderPayHistory(
    val orderId: OrderId,
    val payResult: PayResult?,
)
