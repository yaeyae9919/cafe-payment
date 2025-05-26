package com.cafe.payment.order.application

import com.cafe.payment.billing.external.PayFailure
import com.cafe.payment.fixture.MockOrderItemRepository
import com.cafe.payment.fixture.MockOrderPayConfirmationRepository
import com.cafe.payment.fixture.MockOrderPayHistoryRepository
import com.cafe.payment.fixture.MockOrderRepository
import com.cafe.payment.fixture.MockPayService
import com.cafe.payment.fixture.MockProductRepository
import com.cafe.payment.fixture.OrderFixture
import com.cafe.payment.fixture.PayFixture
import com.cafe.payment.fixture.ProductFixture
import com.cafe.payment.fixture.UserFixture
import com.cafe.payment.order.OrderNotFoundException
import com.cafe.payment.order.OrderPayException
import com.cafe.payment.order.domain.OrderConfirmationStatus
import com.cafe.payment.order.domain.OrderPayConfirmation
import com.cafe.payment.order.domain.OrderStatus
import com.cafe.payment.product.ProductNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.LocalDateTime

class OrderPayUsecaseImplSpec : DescribeSpec({

    val orderRepository = MockOrderRepository()
    val payService = MockPayService()
    val productRepository = MockProductRepository()
    val orderItemRepository = MockOrderItemRepository()
    val orderPayConfirmationRepository = MockOrderPayConfirmationRepository()
    val orderPayHistoryRepository = MockOrderPayHistoryRepository()

    beforeEach {
        orderPayConfirmationRepository.clear()
        orderPayHistoryRepository.clear()
        orderRepository.clear()
        productRepository.clear()
        orderItemRepository.clear()
    }

    val orderPayUsecase =
        OrderPayUsecaseImpl(
            orderRepository = orderRepository,
            orderItemRepository = orderItemRepository,
            productRepository = productRepository,
            orderPayConfirmationRepository = orderPayConfirmationRepository,
            orderPayHistoryRepository = orderPayHistoryRepository,
            payService = payService,
        )

    describe("prepareOrder") {
        context("주문하려는 상품이 존재하지 않으면 에러가 발생해요.") {
            // given
            val buyerId = UserFixture.generateUserId()
            val orderItems =
                listOf(
                    OrderPayUsecase.OrderItem(
                        productId = ProductFixture.generateProductId(),
                        quantity = 1,
                    ),
                )

            // when & then
            shouldThrow<ProductNotFoundException> {
                orderPayUsecase.prepareOrder(buyerId, orderItems)
            }
        }
    }

    describe("orderPay") {
        val buyerId = UserFixture.generateUserId()
        val order = OrderFixture.createOrder(buyerId = buyerId)

        context("주문 관련 오류") {
            it("주문이 존재하지 않다면 에러가 발생해요.") {

                // given
                val requesterId = UserFixture.generateUserId()
                val nonExistentOrderId = OrderFixture.generateOrderId()

                // when & then
                shouldThrow<OrderNotFoundException> {
                    orderPayUsecase.orderPay(requesterId, nonExistentOrderId)
                }
            }

            it("다른 사람의 주문에 대해 결제할 수 없어요.") {
                // given
                orderRepository.save(order)
                val otherUserId = UserFixture.generateUserId()

                // when & then
                shouldThrow<OrderPayException> {
                    orderPayUsecase.orderPay(otherUserId, order.id)
                }
            }
        }

        describe("결제 처리 결과 핸들링") {

            context("결제 에러에 따른 주문 상태 변경") {

                it("Internal Server Error") {
                    // given
                    orderRepository.save(order)

                    // when
                    val failResult = PayFixture.createFailPayResult(PayFailure.InternalServerError())
                    payService.configurePayResult(failResult)

                    // then
                    val orderResult = orderPayUsecase.orderPay(buyerId, order.id)

                    // order
                    val savedOrder = orderRepository.findById(order.id)
                    savedOrder shouldNotBe null
                    savedOrder!!.status shouldBe OrderStatus.PAY_FAILED

                    // order pay confirmation
                    val savedOrderPayConfirmation = orderPayConfirmationRepository.findByOrderId(order.id)
                    savedOrderPayConfirmation shouldBe null

                    // order result
                    orderResult.status shouldBe OrderPayUsecase.OrderPayStatus.FAILED
                }

                it("Timeout Error") {
                    // given
                    orderRepository.save(order)

                    // when
                    val failResult = PayFixture.createFailPayResult(PayFailure.TimeoutError())
                    payService.configurePayResult(failResult)

                    // then
                    val orderResult = orderPayUsecase.orderPay(buyerId, order.id)

                    // order
                    val savedOrder = orderRepository.findById(order.id)
                    savedOrder shouldNotBe null
                    savedOrder!!.status shouldBe OrderStatus.PAY_PROCESSING

                    // order pay confirmation
                    val savedOrderPayConfirmation = orderPayConfirmationRepository.findByOrderId(order.id)
                    savedOrderPayConfirmation shouldBe null

                    // order result
                    orderResult.status shouldBe OrderPayUsecase.OrderPayStatus.FAILED
                }

                it("Already Paid Error") {
                    // given
                    orderRepository.save(order)

                    // when
                    val failResult = PayFixture.createFailPayResult(PayFailure.AlreadyPaidError())
                    payService.configurePayResult(failResult)

                    // then
                    val orderResult = orderPayUsecase.orderPay(buyerId, order.id)

                    // order
                    val savedOrder = orderRepository.findById(order.id)
                    savedOrder shouldNotBe null
                    savedOrder!!.status shouldBe order.status

                    // order pay confirmation
                    val savedOrderPayConfirmation = orderPayConfirmationRepository.findByOrderId(order.id)
                    savedOrderPayConfirmation shouldBe null

                    // order result
                    orderResult.status shouldBe OrderPayUsecase.OrderPayStatus.FAILED
                }
            }

            context("결제 성공") {
                // when
                val successResult = PayFixture.createSuccessPayResult()
                payService.configurePayResult(successResult)

                // then
                val orderResult = orderPayUsecase.orderPay(buyerId, order.id)

                // order
                val savedOrder = orderRepository.findById(order.id)
                savedOrder shouldNotBe null
                savedOrder!!.status shouldBe OrderStatus.PAY_COMPLETED

                // order pay confirmation
                val savedOrderPayConfirmation = orderPayConfirmationRepository.findByOrderId(order.id)
                savedOrderPayConfirmation shouldNotBe null
                savedOrderPayConfirmation!!.status shouldBe OrderConfirmationStatus.PAID

                // order result
                orderResult.status shouldBe OrderPayUsecase.OrderPayStatus.SUCCESS
            }
        }
    }

    describe("orderPayRefund") {
        val buyerId = UserFixture.generateUserId()
        val order = OrderFixture.createOrder(buyerId = buyerId)

        context("주문 관련 오류") {

            it("주문이 존재하지 않다면 에러가 발생해요.") {
                // given
                val requesterId = UserFixture.generateUserId()
                val nonExistentOrderId = OrderFixture.generateOrderId()

                // when & then
                shouldThrow<OrderNotFoundException> {
                    orderPayUsecase.orderPayRefund(requesterId, nonExistentOrderId)
                }
            }

            it("다른 사람의 주문에 대해 취소할 수 없어요.") {
                // given
                orderRepository.save(order)

                val otherUserId = UserFixture.generateUserId()

                // when & then
                shouldThrow<OrderPayException> {
                    orderPayUsecase.orderPayRefund(otherUserId, order.id)
                }
            }

            it("완료되지 않은 주문은 환불할 수 없어요.") {
                // given
                orderRepository.save(order)
                val confirmation =
                    OrderFixture.createConfirmation(
                        order = order,
                    )
                orderPayConfirmationRepository.save(confirmation)

                // when & then
                shouldThrow<OrderPayException> {
                    orderPayUsecase.orderPayRefund(buyerId, order.id)
                }
            }

            it("주문 결제 정보가 없으면 에러가 발생해요.") {
                // given
                val completedOrder = order.payComplete(LocalDateTime.now())
                orderRepository.save(completedOrder)
                // orderConfirmation 저장하지 않음.

                // when & then
                shouldThrow<OrderNotFoundException> {
                    orderPayUsecase.orderPayRefund(buyerId, order.id)
                }
            }

            it("이미 주문 결제 취소된 주문은 취소할 수 없어요.") {
                // given
                val completedOrder = order.payComplete(LocalDateTime.now())
                orderRepository.save(completedOrder)

                val confirmation =
                    OrderFixture.createConfirmation(
                        order = completedOrder,
                        paidAt = LocalDateTime.now(),
                    )
                val canceledConfirmation = confirmation.cancel(LocalDateTime.now())
                orderPayConfirmationRepository.save(canceledConfirmation)

                // when & then
                shouldThrow<OrderPayException> {
                    orderPayUsecase.orderPayRefund(buyerId, order.id)
                }
            }
        }

        describe("환불 처리 결과 핸들링") {

            beforeEach {
                val completedOrder = order.payComplete(LocalDateTime.now())
                orderRepository.save(completedOrder)
                val confirmation =
                    OrderPayConfirmation.paid(
                        paidAt = LocalDateTime.now(),
                        order = completedOrder,
                    )
                orderPayConfirmationRepository.save(confirmation)
            }

            context("환불 에러에 따른 주문 상태 변경") {
                it("Internal Server Error") {
                    // when
                    val failResult = PayFixture.createFailPayResult(PayFailure.InternalServerError())
                    payService.configurePayResult(failResult)

                    // then
                    val orderResult = orderPayUsecase.orderPayRefund(buyerId, order.id)

                    // order
                    val savedOrder = orderRepository.findById(order.id)
                    savedOrder shouldNotBe null
                    savedOrder!!.status shouldBe OrderStatus.CANCEL_FAILED

                    // order pay confirmation (변경되지 않음)
                    val savedOrderPayConfirmation = orderPayConfirmationRepository.findByOrderId(order.id)
                    savedOrderPayConfirmation shouldNotBe null
                    savedOrderPayConfirmation!!.status shouldBe OrderConfirmationStatus.PAID

                    // order result
                    orderResult.status shouldBe OrderPayUsecase.OrderPayStatus.FAILED
                }

                it("Timeout Error") {
                    // when
                    val failResult = PayFixture.createFailPayResult(PayFailure.TimeoutError())
                    payService.configurePayResult(failResult)

                    // then
                    val orderResult = orderPayUsecase.orderPayRefund(buyerId, order.id)

                    // order
                    val savedOrder = orderRepository.findById(order.id)
                    savedOrder shouldNotBe null
                    savedOrder!!.status shouldBe OrderStatus.CANCEL_PROCESSING

                    // order pay confirmation (변경되지 않음)
                    val savedOrderPayConfirmation = orderPayConfirmationRepository.findByOrderId(order.id)
                    savedOrderPayConfirmation shouldNotBe null
                    savedOrderPayConfirmation!!.status shouldBe OrderConfirmationStatus.PAID

                    // order result
                    orderResult.status shouldBe OrderPayUsecase.OrderPayStatus.FAILED
                }
            }

            context("환불 성공") {
                it("환불이 성공하면 주문과 확정 정보가 모두 취소 상태가 되어요.") {
                    // when
                    val successResult = PayFixture.createSuccessPayResult()
                    payService.configurePayResult(successResult)

                    // then
                    val orderResult = orderPayUsecase.orderPayRefund(buyerId, order.id)

                    // order
                    val savedOrder = orderRepository.findById(order.id)
                    savedOrder shouldNotBe null
                    savedOrder!!.status shouldBe OrderStatus.CANCEL_COMPLETED

                    // order pay confirmation
                    val savedOrderPayConfirmation = orderPayConfirmationRepository.findByOrderId(order.id)
                    savedOrderPayConfirmation shouldNotBe null
                    savedOrderPayConfirmation!!.status shouldBe OrderConfirmationStatus.CANCELED

                    // order result
                    orderResult.status shouldBe OrderPayUsecase.OrderPayStatus.SUCCESS
                }
            }
        }
    }
})
