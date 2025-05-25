package com.cafe.payment.order.domain

import com.cafe.payment.fixture.OrderFixture
import com.cafe.payment.order.OrderConfirmationStatusException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class OrderPayConfirmationSpec : FunSpec({

    val order = OrderFixture.createOrder()

    test("주문을 완료할 수 있어요.") {
        val now = LocalDateTime.now()
        val orderPayConfirmation =
            OrderPayConfirmation.create(
                orderId = order.id,
                totalAmount = order.totalAmount,
            )

        val paid = orderPayConfirmation.paid(now)
        paid.status shouldBe OrderConfirmationStatus.PAID
    }

    test("이미 완료된 주문을 또 완료할 수 없어요.") {
        val now = LocalDateTime.now()
        val orderPayConfirmation =
            OrderPayConfirmation.create(
                orderId = order.id,
                totalAmount = order.totalAmount,
            ).paid(now)

        shouldThrow<OrderConfirmationStatusException> {
            orderPayConfirmation.paid(now)
        }
    }

    test("완료된 주문을 취소할 수 있어요.") {
        val now = LocalDateTime.now()
        val paidConfirmation =
            OrderPayConfirmation.create(
                orderId = order.id,
                totalAmount = order.totalAmount,
            ).paid(now)

        val canceled = paidConfirmation.cancel(now)
        canceled.status shouldBe OrderConfirmationStatus.CANCELED
    }

    test("완료되지 않은 주문을 취소할 수 없어요.") {
        val now = LocalDateTime.now()
        val orderPayConfirmation =
            OrderPayConfirmation.create(
                orderId = order.id,
                totalAmount = order.totalAmount,
            )

        shouldThrow<OrderConfirmationStatusException> {
            orderPayConfirmation.cancel(now)
        }
    }

    test("이미 취소된 주문을 또 취소할 수 없어요.") {
        val now = LocalDateTime.now()
        val canceledConfirmation =
            OrderPayConfirmation.create(
                orderId = order.id,
                totalAmount = order.totalAmount,
            ).paid(now).cancel(now)

        shouldThrow<OrderConfirmationStatusException> {
            canceledConfirmation.cancel(now)
        }
    }
})
