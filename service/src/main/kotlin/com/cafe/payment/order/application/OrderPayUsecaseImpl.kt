package com.cafe.payment.order.application

import com.cafe.payment.billing.external.PayClient
import com.cafe.payment.billing.external.PayFailure
import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.domain.OrderPayConfirmation
import com.cafe.payment.order.repository.OrderPayConfirmationRepository
import com.cafe.payment.order.repository.OrderRepository
import com.cafe.payment.user.domain.UserId
import java.time.LocalDateTime

// TODO 하나의 트랜잭션으로 다루기
class OrderPayUsecaseImpl(
    private val orderRepository: OrderRepository,
    private val orderPayConfirmationRepository: OrderPayConfirmationRepository,
    private val payClient: PayClient,
) : OrderPayUsecase {
    override fun orderAndPay(
        buyerId: UserId,
        orderItems: List<OrderItem>,
    ): OrderId {
        val now = LocalDateTime.now()

        // 1. 주문 생성
        val order =
            Order.create(
                // TODO: ID generator 사용하기
                id = OrderId(System.currentTimeMillis()),
                buyerId = buyerId,
                items = orderItems,
                now = now,
            )

        // 2. 결제 시도
        val payResult =
            payClient.pay(
                orderId = order.id,
                totalAmount = order.totalAmount,
            )

        // 3. 결제 시도에 따른 order 변경
        val processedOrder =
            payResult.fold(
                onSuccess = { result ->
                    val confirmedAt = result.transactionAt

                    val confirmation =
                        OrderPayConfirmation.paid(
                            payId = result.payId,
                            paidAt = confirmedAt,
                            order = order,
                        )
                    orderPayConfirmationRepository.save(confirmation)
                    order.payComplete(confirmedAt)
                },
                onFailure = { exception ->
                    val exceptionOccurredAt = LocalDateTime.now()

                    when (exception) {
                        is PayFailure.InternalServerError -> {
                            // TODO 에러 로그
                            order.payFailed(exceptionOccurredAt)
                        }
                        is PayFailure.TimeoutError -> {
                            // TODO 워닝 로그
                            order.payProcessing(exceptionOccurredAt)
                        }
                        else -> {
                            throw exception
                        }
                    }
                },
            )

        // 4. 주문 정보 저장
        orderRepository.save(processedOrder)

        // TODO 5. 내역 남기기 (pay result > transaction id 있다면 함께 남겨야함)

        return processedOrder.id
    }
}
