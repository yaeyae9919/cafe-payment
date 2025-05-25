package com.cafe.payment.order.application

import com.cafe.payment.billing.external.PayClient
import com.cafe.payment.billing.external.PayFailure
import com.cafe.payment.library.generator.LongIdGenerator
import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.domain.OrderPayConfirmation
import com.cafe.payment.order.repository.OrderPayConfirmationRepository
import com.cafe.payment.order.repository.OrderRepository
import com.cafe.payment.user.domain.UserId
import mu.KotlinLogging
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
                id = OrderId(LongIdGenerator.generate()),
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
                            logger.error(exception) { "결제 실패했어요." }
                            order.payFailed(exceptionOccurredAt)
                        }
                        is PayFailure.TimeoutError -> {
                            logger.error(exception) { "결제 지연이 생겼어요." }
                            order.payProcessing(exceptionOccurredAt)
                        }
                        else -> {
                            logger.error(exception) { "알 수 없는 에러가 발생했어요." }
                            throw exception
                        }
                    }
                },
            )

        // 4. 주문 정보 저장
        orderRepository.save(processedOrder)

        // TODO 5. 내역 남기기 (pay result > transaction id 있다면 함께 남기기)

        return processedOrder.id
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
