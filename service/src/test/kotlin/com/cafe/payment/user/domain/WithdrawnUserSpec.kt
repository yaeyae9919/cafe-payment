package com.cafe.payment.user.domain

import com.cafe.payment.fixture.UserFixture
import com.cafe.payment.user.UserStatusException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class WithdrawnUserSpec : DescribeSpec({

    describe("탈퇴 사용자 생성") {
        context("정상적인 사용자로부터 탈퇴 사용자를 생성할 수 있다") {
            val user = UserFixture.createUser()
            val withdrawTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0)

            val withdrawnUser = WithdrawnUser.create(user, withdrawTime)

            withdrawnUser.user shouldBe user
            withdrawnUser.userId shouldBe user.id
            withdrawnUser.withdrawnAt shouldBe withdrawTime
            withdrawnUser.accountRevokableDate shouldBe withdrawTime.toLocalDate().plusDays(WithdrawnUser.WITHDRAWAL_REVOKE_DAYS)
        }
    }

    describe("탈퇴 철회 가능 여부 판단") {
        val withdrawTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
        val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

        context("탈퇴 후 ${WithdrawnUser.WITHDRAWAL_REVOKE_DAYS}일 이내인 경우") {
            it("탈퇴 철회가 가능하다") {
                val checkTime = withdrawTime.plusDays(WithdrawnUser.WITHDRAWAL_REVOKE_DAYS - 1L)

                withdrawnUser.cannotRevokable(checkTime) shouldBe false
            }

            it("정확히 ${WithdrawnUser.WITHDRAWAL_REVOKE_DAYS}일째도 탈퇴 철회가 가능하다") {
                val checkTime = withdrawTime.plusDays(WithdrawnUser.WITHDRAWAL_REVOKE_DAYS)

                withdrawnUser.cannotRevokable(checkTime) shouldBe false
            }
        }

        context("탈퇴 후 ${WithdrawnUser.WITHDRAWAL_REVOKE_DAYS}일이 지난 경우") {
            it("탈퇴 철회가 불가능하다") {
                val checkTime = withdrawTime.plusDays(WithdrawnUser.WITHDRAWAL_REVOKE_DAYS + 1L)

                withdrawnUser.cannotRevokable(checkTime) shouldBe true
            }
        }
    }

    describe("탈퇴 철회") {
        context("탈퇴 후 ${WithdrawnUser.WITHDRAWAL_REVOKE_DAYS}일 이내 탈퇴 철회할 수 있다") {

            it("탈퇴 후 ${WithdrawnUser.WITHDRAWAL_REVOKE_DAYS}일 이전") {
                val withdrawTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
                val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

                val revokeTime = withdrawTime.plusDays(WithdrawnUser.WITHDRAWAL_REVOKE_DAYS - 1L)
                val revokedUser = withdrawnUser.toRevokedUser(revokeTime)

                revokedUser shouldBe withdrawnUser.user
            }

            it("정확히 ${WithdrawnUser.WITHDRAWAL_REVOKE_DAYS}일 째되는 날") {
                val withdrawTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
                val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

                val revokeTime = withdrawTime.plusDays(WithdrawnUser.WITHDRAWAL_REVOKE_DAYS)
                val revokedUser = withdrawnUser.toRevokedUser(revokeTime)

                revokedUser shouldBe withdrawnUser.user
            }

            it("같은 날에도 탈퇴를 철회할 수 있다") {
                // 새벽 1시 탈퇴
                val withdrawTime = LocalDateTime.of(2024, 1, 1, 1, 0, 0)
                // 밤 11시 철회
                val revokeTime = LocalDateTime.of(2024, 1, 31, 23, 0, 0)
                val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

                val revokedUser = withdrawnUser.toRevokedUser(revokeTime)

                revokedUser shouldBe withdrawnUser.user
            }
        }

        context("탈퇴 후 ${WithdrawnUser.WITHDRAWAL_REVOKE_DAYS}일 지난 이후엔 탈퇴 철회할 수 없다") {
            it("탈퇴 철회 시도 시 예외가 발생한다") {
                val withdrawTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
                val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

                val revokeTime = withdrawTime.plusDays(WithdrawnUser.WITHDRAWAL_REVOKE_DAYS + 1L)

                shouldThrow<UserStatusException> {
                    withdrawnUser.toRevokedUser(revokeTime)
                }
            }
        }
    }
})
