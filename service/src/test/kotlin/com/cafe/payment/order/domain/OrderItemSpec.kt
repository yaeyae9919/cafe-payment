package com.cafe.payment.order.domain

import com.cafe.payment.fixture.OrderFixture
import com.cafe.payment.fixture.ProductFixture
import com.cafe.payment.order.InvalidOrderItemException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class OrderItemSpec : DescribeSpec({

    describe("주문 상품 정보 생성") {
        context("주문 상품 정보를 생성할 수 있다.") {
            val validOrderItemId = OrderFixture.generateOrderItemId()
            val validOrderId = OrderFixture.generateOrderId()
            val validProductId = ProductFixture.generateProductId()
            val validProductName = "아메리카노"
            val validProductAmount = BigDecimal("4500")
            val validQuantity = 3

            val orderItem =
                OrderItem(
                    id = validOrderItemId,
                    orderId = validOrderId,
                    productId = validProductId,
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
                        id = OrderFixture.generateOrderItemId(),
                        orderId = OrderFixture.generateOrderId(),
                        product = ProductFixture.createProduct(),
                        quantity = 0,
                    )
                }

                shouldThrow<InvalidOrderItemException> {
                    OrderItem.create(
                        id = OrderFixture.generateOrderItemId(),
                        orderId = OrderFixture.generateOrderId(),
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
                    id = OrderFixture.generateOrderItemId(),
                    orderId = OrderFixture.generateOrderId(),
                    product = product,
                    quantity = quantity,
                )

            orderItem.totalAmount shouldBe product.amount.multiply(BigDecimal.valueOf(quantity.toLong()))
        }
    }
})
