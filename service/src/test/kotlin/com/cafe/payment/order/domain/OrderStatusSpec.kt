package com.cafe.payment.order.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OrderStatusSpec : FunSpec({

    context("PAY_PENDING 상태에서") {
        test("PAY_PROCESSING으로 변경할 수 있어요") {
            OrderStatus.PAY_PENDING.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe true
        }

        test("PAY_FAILED로 변경할 수 있어요") {
            OrderStatus.PAY_PENDING.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe true
        }

        test("PAY_COMPLETED로 변경할 수 있어요") {
            OrderStatus.PAY_PENDING.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe true
        }

        test("PAY_PENDING으로 변경할 수 없어요") {
            OrderStatus.PAY_PENDING.canTransitionTo(OrderStatus.PAY_PENDING) shouldBe false
        }
    }

    context("PAY_PROCESSING 상태에서") {
        test("PAY_COMPLETED로 변경할 수 있어요") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe true
        }

        test("PAY_FAILED로 변경할 수 있어요") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe true
        }

        test("PAY_PENDING으로 변경할 수 없어요") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.PAY_PENDING) shouldBe false
        }

        test("PAY_PROCESSING으로 변경할 수 없어요") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe false
        }
    }

    context("PAY_FAILED 상태에서") {
        test("PAY_PROCESSING으로 변경할 수 있어요 (재시도)") {
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe true
        }

        test("PAY_COMPLETED로 변경할 수 있어요") {
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe true
        }

        test("PAY_PENDING으로 변경할 수 없어요") {
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.PAY_PENDING) shouldBe false
        }

        test("PAY_FAILED로 변경할 수 없어요") {
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe false
        }
    }

    context("PAY_COMPLETED 상태에서") {
        test("어떤 상태로도 변경할 수 없어요 (최종 상태)") {
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.PAY_PENDING) shouldBe false
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe false
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe false
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe false
        }
    }
})
