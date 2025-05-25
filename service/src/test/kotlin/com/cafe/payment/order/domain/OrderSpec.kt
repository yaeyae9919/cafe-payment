package com.cafe.payment.order.domain

import com.cafe.payment.fixture.OrderFixture
import com.cafe.payment.fixture.ProductFixture
import com.cafe.payment.fixture.UserFixture
import com.cafe.payment.order.InvalidOrderItemException
import com.cafe.payment.product.domain.ProductId
import com.cafe.payment.user.domain.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.LocalDateTime

class OrderSpec : DescribeSpec({

    describe("주문 생성") {
        context("주문을 생성할 수 있다.") {
            val validOrderId = OrderFixture.generateOrderId()
            val validBuyerId = UserFixture.generateUserId()
            val validOrderItem = OrderFixture.createOrderItem()
            val now = LocalDateTime.now()

            val order =
                Order.create(
                    id = validOrderId,
                    buyerId = validBuyerId,
                    items = listOf(validOrderItem),
                    now = now,
                )

            order.id shouldBe validOrderId
            order.buyerId shouldBe validBuyerId
            order.items shouldBe listOf(validOrderItem)
            order.createdAt shouldBe now
        }

        context("주문 생성은 실패할 수 있다. - validation") {
            it("주문할 상품이 없는 경우 실패") {
                val validOrderId = OrderId(1L)
                val validBuyerId = UserId(1L)
                val now = LocalDateTime.now()

                shouldThrow<InvalidOrderItemException> {
                    Order.create(
                        id = validOrderId,
                        buyerId = validBuyerId,
                        items = emptyList(),
                        now = now,
                    )
                }
            }
        }

        context("주문 내역 금액은 주문 상품 금액의 총 합이다.") {
            val validOrderId = OrderId(1L)
            val validBuyerId = UserId(1L)
            val now = LocalDateTime.now()

            val orderItems =
                listOf(
                    OrderFixture.createOrderItem(),
                    OrderFixture.createOrderItem(),
                )
            val order =
                Order.create(
                    id = validOrderId,
                    buyerId = validBuyerId,
                    items = orderItems,
                    now = now,
                )

            order.totalAmount shouldBe orderItems.sumOf { it.totalAmount }
        }
    }

    describe("주문 상품 정보 생성") {
        context("주문 상품 정보를 생성할 수 있다.") {

            val validProductId = ProductId(1L)
            val validProductName = "아메리카노"
            val validProductAmount = BigDecimal("4500")
            val validQuantity = 3

            val orderItem =
                OrderItem(
                    productId = ProductId(1L),
                    quantity = validQuantity,
                    productName = validProductName,
                    amount = validProductAmount,
                )

            orderItem.productId shouldBe validProductId
            orderItem.quantity shouldBe validQuantity
            orderItem.productName shouldBe validProductName
            orderItem.amount shouldBe validProductAmount
        }

        context("주문 상품 정보 생성은 실패할 수 있다. - validation") {
            it("상품은 최소 1개 이상 주문해야한다.") {
                shouldThrow<InvalidOrderItemException> {
                    OrderItem.create(
                        product = ProductFixture.createProduct(),
                        quantity = 0,
                    )
                }

                shouldThrow<InvalidOrderItemException> {
                    OrderItem.create(
                        product = ProductFixture.createProduct(),
                        quantity = -1,
                    )
                }
            }
        }

        context("주문 상품의 총 금액은 상품 단가 * 개수이다.") {
            val product = ProductFixture.createProduct()
            val quantity = 3
            val orderItem =
                OrderItem.create(
                    product = product,
                    quantity = quantity,
                )

            orderItem.totalAmount shouldBe product.amount.multiply(BigDecimal.valueOf(quantity.toLong()))
        }
    }
})
