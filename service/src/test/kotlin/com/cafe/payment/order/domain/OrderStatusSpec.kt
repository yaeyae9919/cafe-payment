package com.cafe.payment.order.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class OrderStatusSpec : FunSpec({

    context("PENDING 상태에서") {
        test("PAY_PROCESSING으로 변경할 수 있어요") {
            OrderStatus.PENDING.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe true
        }

        test("PAY_FAILED로 변경할 수 있어요") {
            OrderStatus.PENDING.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe true
        }

        test("PAY_COMPLETED로 변경할 수 있어요") {
            OrderStatus.PENDING.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe true
        }

        test("PAY_PENDING으로 변경할 수 없어요") {
            OrderStatus.PENDING.canTransitionTo(OrderStatus.PENDING) shouldBe false
        }

        test("CANCEL 관련 상태로 변경할 수 없어요") {
            OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCEL_PROCESSING) shouldBe false
            OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCEL_FAILED) shouldBe false
            OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCEL_COMPLETED) shouldBe false
        }
    }

    context("PAY_PROCESSING 상태에서") {
        test("PAY_COMPLETED로 변경할 수 있어요") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe true
        }

        test("PAY_FAILED로 변경할 수 있어요") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe true
        }

        test("PAY_PROCESSING으로 변경할 수 있어요 (타임아웃 재시도)") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe true
        }

        test("PAY_PENDING으로 변경할 수 없어요") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.PENDING) shouldBe false
        }

        test("CANCEL 관련 상태로 변경할 수 없어요") {
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.CANCEL_PROCESSING) shouldBe false
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.CANCEL_FAILED) shouldBe false
            OrderStatus.PAY_PROCESSING.canTransitionTo(OrderStatus.CANCEL_COMPLETED) shouldBe false
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
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.PENDING) shouldBe false
        }

        test("PAY_FAILED로 변경할 수 없어요") {
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe false
        }

        test("CANCEL 관련 상태로 변경할 수 없어요") {
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.CANCEL_PROCESSING) shouldBe false
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.CANCEL_FAILED) shouldBe false
            OrderStatus.PAY_FAILED.canTransitionTo(OrderStatus.CANCEL_COMPLETED) shouldBe false
        }
    }

    context("PAY_COMPLETED 상태에서") {
        test("CANCEL_PROCESSING으로 변경할 수 있어요") {
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.CANCEL_PROCESSING) shouldBe true
        }

        test("CANCEL_FAILED로 변경할 수 있어요") {
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.CANCEL_FAILED) shouldBe true
        }

        test("CANCEL_COMPLETED로 변경할 수 있어요") {
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.CANCEL_COMPLETED) shouldBe true
        }

        test("PAY 관련 상태로 변경할 수 없어요") {
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.PENDING) shouldBe false
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe false
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe false
            OrderStatus.PAY_COMPLETED.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe false
        }
    }

    context("CANCEL_PROCESSING 상태에서") {
        test("CANCEL_COMPLETED로 변경할 수 있어요") {
            OrderStatus.CANCEL_PROCESSING.canTransitionTo(OrderStatus.CANCEL_COMPLETED) shouldBe true
        }

        test("CANCEL_FAILED로 변경할 수 있어요") {
            OrderStatus.CANCEL_PROCESSING.canTransitionTo(OrderStatus.CANCEL_FAILED) shouldBe true
        }

        test("CANCEL_PROCESSING으로 변경할 수 있어요 (타임아웃 재시도)") {
            OrderStatus.CANCEL_PROCESSING.canTransitionTo(OrderStatus.CANCEL_PROCESSING) shouldBe true
        }

        test("PAY 관련 상태로 변경할 수 없어요") {
            OrderStatus.CANCEL_PROCESSING.canTransitionTo(OrderStatus.PENDING) shouldBe false
            OrderStatus.CANCEL_PROCESSING.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe false
            OrderStatus.CANCEL_PROCESSING.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe false
            OrderStatus.CANCEL_PROCESSING.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe false
        }
    }

    context("CANCEL_FAILED 상태에서") {
        test("CANCEL_PROCESSING으로 변경할 수 있어요 (재시도)") {
            OrderStatus.CANCEL_FAILED.canTransitionTo(OrderStatus.CANCEL_PROCESSING) shouldBe true
        }

        test("CANCEL_COMPLETED로 변경할 수 있어요") {
            OrderStatus.CANCEL_FAILED.canTransitionTo(OrderStatus.CANCEL_COMPLETED) shouldBe true
        }

        test("CANCEL_FAILED로 변경할 수 없어요") {
            OrderStatus.CANCEL_FAILED.canTransitionTo(OrderStatus.CANCEL_FAILED) shouldBe false
        }

        test("PAY 관련 상태로 변경할 수 없어요") {
            OrderStatus.CANCEL_FAILED.canTransitionTo(OrderStatus.PENDING) shouldBe false
            OrderStatus.CANCEL_FAILED.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe false
            OrderStatus.CANCEL_FAILED.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe false
            OrderStatus.CANCEL_FAILED.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe false
        }
    }

    context("CANCEL_COMPLETED 상태에서") {
        test("어떤 상태로도 변경할 수 없어요 (최종 상태)") {
            OrderStatus.CANCEL_COMPLETED.canTransitionTo(OrderStatus.PENDING) shouldBe false
            OrderStatus.CANCEL_COMPLETED.canTransitionTo(OrderStatus.PAY_PROCESSING) shouldBe false
            OrderStatus.CANCEL_COMPLETED.canTransitionTo(OrderStatus.PAY_FAILED) shouldBe false
            OrderStatus.CANCEL_COMPLETED.canTransitionTo(OrderStatus.PAY_COMPLETED) shouldBe false
            OrderStatus.CANCEL_COMPLETED.canTransitionTo(OrderStatus.CANCEL_PROCESSING) shouldBe false
            OrderStatus.CANCEL_COMPLETED.canTransitionTo(OrderStatus.CANCEL_FAILED) shouldBe false
            OrderStatus.CANCEL_COMPLETED.canTransitionTo(OrderStatus.CANCEL_COMPLETED) shouldBe false
        }
    }
})
