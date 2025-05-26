package com.cafe.payment.order.domain

import com.cafe.payment.fixture.OrderFixture
import com.cafe.payment.fixture.PayFixture
import com.cafe.payment.fixture.UserFixture
import com.cafe.payment.order.InvalidOrderItemException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class OrderSpec : DescribeSpec({

    describe("주문 생성") {
        context("주문을 생성할 수 있다.") {
            val validOrderId = OrderFixture.generateOrderId()
            val validPayId = PayFixture.generatePayId()
            val validBuyerId = UserFixture.generateUserId()
            val validOrderItems = listOf(OrderFixture.createOrderItem())
            val now = LocalDateTime.now()

            val order =
                Order.create(
                    id = validOrderId,
                    payId = validPayId,
                    buyerId = validBuyerId,
                    orderItems = validOrderItems,
                    now = now,
                )

            order.id shouldBe validOrderId
            order.buyerId shouldBe validBuyerId
            order.itemIds shouldBe validOrderItems.map { it.id }
            order.createdAt shouldBe now
        }

        context("주문 생성은 실패할 수 있다. - validation") {
            it("주문할 상품이 없는 경우 실패") {
                val validOrderId = OrderFixture.generateOrderId()
                val validPayId = PayFixture.generatePayId()
                val validBuyerId = UserFixture.generateUserId()
                val now = LocalDateTime.now()

                shouldThrow<InvalidOrderItemException> {
                    Order.create(
                        id = validOrderId,
                        payId = validPayId,
                        buyerId = validBuyerId,
                        orderItems = listOf(),
                        now = now,
                    )
                }
            }
        }
    }
})
