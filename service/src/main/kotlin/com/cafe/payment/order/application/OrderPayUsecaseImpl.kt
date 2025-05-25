package com.cafe.payment.order.application

import com.cafe.payment.billing.application.PayService
import com.cafe.payment.billing.external.PayFailure
import com.cafe.payment.library.generator.LongIdGenerator
import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.domain.OrderPayConfirmation
import com.cafe.payment.order.repository.OrderPayConfirmationRepository
import com.cafe.payment.order.repository.OrderRepository
import com.cafe.payment.product.ProductNotFoundException
import com.cafe.payment.product.repository.ProductRepository
import com.cafe.payment.user.domain.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OrderPayUsecaseImpl(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val orderPayConfirmationRepository: OrderPayConfirmationRepository,
    private val payService: PayService,
) : OrderPayUsecase {
    override fun orderAndPay(
        buyerId: UserId,
        orderItems: List<OrderPayUsecase.OrderItem>,
    ): OrderPayUsecase.OrderPayResult {
        val now = LocalDateTime.now()

        // 0. 상품 정보 확인
        val products = productRepository.findByIds(orderItems.map { it.productId }).associateBy { it.id }

        // 1. 주문 생성
        val order =
            Order.create(
                id = OrderId(LongIdGenerator.generate()),
                buyerId = buyerId,
                items =
                    orderItems.map {
                        val product = products[it.productId] ?: throw ProductNotFoundException.notFound(it.productId)
                        OrderItem(
                            productId = product.id,
                            productName = product.name,
                            quantity = it.quantity,
                            amount = product.amount,
                        )
                    },
                now = now,
            )

        // 2. 결제 시도
        val payResult = payService.pay(order)

        // 3. 결제 시도에 따른 최종 order 설정 및 결과 생성
        val (processedOrder, orderPayResult) =
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
                    val completedOrder = order.payComplete(confirmedAt)

                    val orderResult =
                        OrderPayUsecase.OrderPayResult(
                            orderId = completedOrder.id,
                            status = OrderPayUsecase.OrderPayStatus.SUCCESS,
                        )

                    completedOrder to orderResult
                },
                onFailure = { exception ->
                    val exceptionOccurredAt = LocalDateTime.now()

                    val orderByCase =
                        when (exception) {
                            is PayFailure.InternalServerError -> {
                                logger.error(exception) { "결제 실패했어요." }
                                val failedOrder = order.payFailed(exceptionOccurredAt)

                                failedOrder
                            }

                            is PayFailure.TimeoutError -> {
                                logger.error(exception) { "결제 지연이 생겼어요." }

                            /*
                             * TODO
                             *  결제 지연 이벤트를 발행해 결제 상태를 계속 확인
                             *  결제 완료 되었을 경우, 해당 결제 취소 처리 (사용자는 주문 실패로 간주하기 때문)
                             *  결제 실패 되었을 경우, 별도 결제 서버 호출 없이 Order 의 상태만 변경할 것.
                             */
                                val processingOrder = order.payProcessing(exceptionOccurredAt)

                                processingOrder
                            }

                            else -> {
                                logger.error(exception) { "알 수 없는 에러가 발생했어요. 확인이 필요해요." }
                                throw exception
                            }
                        }

                    // 결제 지연이더라도 사용자에게 주문은 실패로 보이도록 해요.
                    val orderResult =
                        OrderPayUsecase.OrderPayResult(
                            orderId = orderByCase.id,
                            status = OrderPayUsecase.OrderPayStatus.FAILED,
                        )
                    orderByCase to orderResult
                },
            )

        // 4. 주문 정보 저장
        orderRepository.save(processedOrder)

        // TODO 5. 내역 남기기 (pay result > transaction id 있다면 함께 남기기)

        return orderPayResult
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
