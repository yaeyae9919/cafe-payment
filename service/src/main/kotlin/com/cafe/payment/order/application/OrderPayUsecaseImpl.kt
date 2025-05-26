package com.cafe.payment.order.application

import com.cafe.payment.billing.application.PayService
import com.cafe.payment.billing.external.PayFailure
import com.cafe.payment.library.generator.LongIdGenerator
import com.cafe.payment.order.OrderNotFoundException
import com.cafe.payment.order.OrderPayException
import com.cafe.payment.order.domain.Order
import com.cafe.payment.order.domain.OrderId
import com.cafe.payment.order.domain.OrderItem
import com.cafe.payment.order.domain.OrderItemId
import com.cafe.payment.order.domain.OrderPayConfirmation
import com.cafe.payment.order.domain.OrderPayHistory
import com.cafe.payment.order.repository.OrderItemRepository
import com.cafe.payment.order.repository.OrderPayConfirmationRepository
import com.cafe.payment.order.repository.OrderPayHistoryRepository
import com.cafe.payment.order.repository.OrderRepository
import com.cafe.payment.product.ProductNotFoundException
import com.cafe.payment.product.repository.ProductRepository
import com.cafe.payment.user.domain.UserId
import mu.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class OrderPayUsecaseImpl(
    private val orderRepository: OrderRepository,
    private val orderItemRepository: OrderItemRepository,
    private val productRepository: ProductRepository,
    private val orderPayConfirmationRepository: OrderPayConfirmationRepository,
    private val orderPayHistoryRepository: OrderPayHistoryRepository,
    private val payService: PayService,
) : OrderPayUsecase {
    override fun prepareOrder(
        buyerId: UserId,
        orderItems: List<OrderPayUsecase.OrderItem>,
    ): OrderId {
        val now = LocalDateTime.now()

        // 0. 상품 정보 확인
        val products = productRepository.findByIds(orderItems.map { it.productId }).associateBy { it.id }
        val orderId = OrderId(LongIdGenerator.generate())

        val payId = payService.obtainPayId(orderId)

        // 1. 주문 상품 목록 및 주문 생성 및 저장
        val items =
            orderItems.map {
                val product = products[it.productId] ?: throw ProductNotFoundException.notFound(it.productId)
                OrderItem(
                    id = OrderItemId(LongIdGenerator.generate()),
                    orderId = orderId,
                    productId = product.id,
                    productName = product.name,
                    quantity = it.quantity,
                    amount = product.amount,
                )
            }
        orderItemRepository.saveAll(items)

        val order =
            Order.create(
                id = orderId,
                payId = payId,
                buyerId = buyerId,
                orderItems = items,
                now = now,
            )

        return orderRepository.save(order).id
    }

    override fun orderPay(
        requesterId: UserId,
        orderId: OrderId,
    ): OrderPayUsecase.OrderPayResult {
        val now = LocalDateTime.now()

        // 1. 주문 정보 확인
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException.notFoundOrder(orderId)
        if (order.isBuyer(requesterId).not()) throw OrderPayException.isNotBuyer(requesterId)

        // 2. 결제 시도
        val paid = payService.pay(order)

        // 3. 결제 시도에 따른 최종 order 설정 및 결과 생성
        val (processedOrder, payResult) =
            paid.fold(
                onSuccess = { result ->
                    val confirmedAt = result.transactionAt

                    val confirmation =
                        OrderPayConfirmation.paid(
                            order = order,
                            paidAt = confirmedAt,
                        )
                    orderPayConfirmationRepository.save(confirmation)
                    val completedOrder = order.payComplete(confirmedAt)
                    completedOrder to result
                },
                onFailure = { exception ->

                    val orderByCase =
                        when (exception) {
                            is PayFailure.InternalServerError -> {
                                logger.error(exception) { "결제 실패했어요." }
                                val failedOrder = order.payFailed(now)
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
                                val processingOrder = order.payProcessing(now)
                                processingOrder
                            }

                            is PayFailure.AlreadyPaidError -> {
                                logger.error(exception) { "이미 결제되었어요." }
                                // 이미 정상 처리된 건이므로 별도의 상태를 변경하지 않아요.
                                order
                            }

                            else -> {
                                logger.error(exception) { "알 수 없는 에러가 발생했어요. 확인이 필요해요." }
                                throw exception
                            }
                        }
                    orderByCase to null
                },
            )

        // 4. 상태에 따라 변경된 주문 정보 저장
        /**
         * TODO 낙관락 도입으로 인한 에러 케이스 핸들링
         *
         * 낙관락 충돌로 인한 order 정합성 깨짐 이슈
         *  t1 : pending (v1)
         *  t2 : pending (v2)
         *
         *  t1 : 결제 성공
         *  t2 : 중복 결제
         *
         *   ~ 모종의 이유로 t1 지연됨 ~
         *
         *  t2 : pending 저장 (v1 -> v2)
         *  t1 : complete 저장 실패 (낙관락 충돌)
         */
        orderRepository.save(processedOrder)

        // 5. 내역 남기기
        orderPayHistoryRepository.record(
            OrderPayHistory.create(processedOrder, payResult),
        )
        return OrderPayUsecase.OrderPayResult(
            orderId = processedOrder.id,
            // 결제 지연으로 인한 경우에도, 사용자에게 주문은 "실패"로 보이도록 해요.
            status = if (payResult != null) OrderPayUsecase.OrderPayStatus.SUCCESS else OrderPayUsecase.OrderPayStatus.FAILED,
        )
    }

    override fun orderPayRefund(
        requesterId: UserId,
        orderId: OrderId,
    ): OrderPayUsecase.OrderPayResult {
        val now = LocalDateTime.now()

        // 1. 주문 확인
        val order = orderRepository.findById(orderId) ?: throw OrderNotFoundException.notFoundOrder(orderId)
        if (order.isBuyer(requesterId).not()) throw OrderPayException.isNotBuyer(requesterId)

        if (order.isNotPayComplete()) {
            throw OrderPayException.cancleOnlyWhenOrderCompleted()
        }

        // 2. 주문 결제 상태 확인
        val orderConfirmation =
            orderPayConfirmationRepository.findByOrderId(orderId) ?: throw OrderNotFoundException.notFoundConfirmation(
                orderId,
            )

        // 결제된 주문만 취소할 수 있어요.
        if (orderConfirmation.isCanceled()) {
            throw OrderPayException.cancleOnlyWhenOrderPaid()
        }

        // 3. 환불 시도
        val refunded = payService.refund(order)

        // 4. 환불 시도에 따른 최종 order 설정 및 결과 생성
        val (processedOrder, refundResult) =
            refunded.fold(
                onSuccess = { result ->
                    val refundedAt = result.transactionAt

                    val canceledConfirmation = orderConfirmation.cancel(refundedAt)
                    orderPayConfirmationRepository.save(canceledConfirmation)

                    val canceledOrder = order.cancelCompleted(refundedAt)
                    canceledOrder to result
                },
                onFailure = { exception ->
                    val orderByCase =
                        when (exception) {
                            is PayFailure.InternalServerError -> {
                                logger.error(exception) { "환불 실패했어요." }
                                val failedOrder = order.cancelFailed(now)
                                failedOrder
                            }

                            is PayFailure.TimeoutError -> {
                                logger.error(exception) { "환불 지연이 생겼어요." }

                                /*
                                 * TODO
                                 *  환불 지연 이벤트를 발행해 환불 상태를 계속 확인
                                 *  환불 완료 되었을 경우, 해당 환불 취소 처리 (사용자는 환불 실패로 간주하기 때문)
                                 *  환불 실패 되었을 경우, 별도 결제 서버 호출 없이 Order 의 상태만 변경할 것.
                                 */
                                val processingOrder = order.cancelProcessing(now)
                                processingOrder
                            }

                            else -> {
                                logger.error(exception) { "알 수 없는 에러가 발생했어요. 확인이 필요해요." }
                                throw exception
                            }
                        }
                    orderByCase to null
                },
            )

        // 5. 상태에 따라 변경된 주문 정보 저장
        orderRepository.save(processedOrder)

        // 6. 내역 남기기
        orderPayHistoryRepository.record(
            OrderPayHistory.create(processedOrder, refundResult),
        )

        return OrderPayUsecase.OrderPayResult(
            orderId = processedOrder.id,
            // 환불 지연으로 인한 경우에도, 사용자에게 취소는 "실패"로 보이도록 해요.
            status = if (refundResult != null) OrderPayUsecase.OrderPayStatus.SUCCESS else OrderPayUsecase.OrderPayStatus.FAILED,
        )
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
