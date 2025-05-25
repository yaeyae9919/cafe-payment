package com.cafe.payment.order.domain

import com.cafe.payment.fixture.OrderFixture
import com.cafe.payment.fixture.PayFixture
import com.cafe.payment.order.OrderConfirmationStatusException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class OrderPayConfirmationSpec : FunSpec({

    val order = OrderFixture.createOrder()

    test("주문을 완료할 수 있어요.") {
        val now = LocalDateTime.now()

        val paid =
            OrderPayConfirmation.paid(
                payId = PayFixture.generatePayId(),
                paidAt = now,
                order = order,
            )

        paid.status shouldBe OrderConfirmationStatus.PAID
    }

    test("완료된 주문을 취소할 수 있어요.") {
        val now = LocalDateTime.now()
        val paid =
            OrderPayConfirmation.paid(
                payId = PayFixture.generatePayId(),
                paidAt = now,
                order = order,
            )

        val canceled = paid.cancel(now)
        canceled.status shouldBe OrderConfirmationStatus.CANCELED
    }

    test("이미 취소된 주문을 또 취소할 수 없어요.") {
        val now = LocalDateTime.now()
        val canceled =
            OrderPayConfirmation.paid(
                payId = PayFixture.generatePayId(),
                paidAt = now,
                order = order,
            ).cancel(now)

        shouldThrow<OrderConfirmationStatusException> {
            canceled.cancel(now)
        }
    }
})
