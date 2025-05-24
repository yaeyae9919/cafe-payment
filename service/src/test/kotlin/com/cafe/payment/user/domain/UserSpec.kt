package com.cafe.payment.user.domain

import com.cafe.payment.fixture.UserFixture
import com.cafe.payment.user.InvalidUserDataException
import com.cafe.payment.user.UserStatusException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import java.time.LocalDateTime

class UserSpec : DescribeSpec({

    describe("사용자 생성") {
        val validUserId = UserId(1L)
        val validName = "홍길동"
        val validPhoneNumber = "01012345678"
        val validGender = Gender.MALE
        val validBirthDate = LocalDate.of(1990, 1, 1)
        val now = LocalDateTime.now()

        context("유효한 데이터로 사용자를 생성할 때 - 성공") {
            val user =
                User.create(
                    id = validUserId,
                    name = validName,
                    phoneNumber = validPhoneNumber,
                    gender = validGender,
                    birthDate = validBirthDate,
                    now = now,
                )

            user.id shouldBe validUserId
            user.name shouldBe validName
            user.phoneNumber shouldBe validPhoneNumber
            user.gender shouldBe validGender
            user.birthDate shouldBe validBirthDate
            user.createdAt shouldBe now
            user.updatedAt shouldBe now
            user.withdrawnAt shouldBe null
            user.status shouldBe UserStatus.ACTIVE
        }

        context("유효하지 않은 데이터로 사용자를 생성하는 경우 - 실패") {
            it("이름이 공백인 경우") {
                shouldThrow<InvalidUserDataException> {
                    User.create(
                        id = validUserId,
                        name = "   ",
                        phoneNumber = validPhoneNumber,
                        gender = validGender,
                        birthDate = validBirthDate,
                    )
                }
            }

            it("전화번호가 공백인 경우") {
                shouldThrow<InvalidUserDataException> {
                    User.create(
                        id = validUserId,
                        name = validName,
                        phoneNumber = "   ",
                        gender = validGender,
                        birthDate = validBirthDate,
                    )
                }
            }

            it("전화번호에 숫자가 아닌 문자가 포함된 경우") {
                shouldThrow<InvalidUserDataException> {
                    User.create(
                        id = validUserId,
                        name = validName,
                        phoneNumber = "010-1234-5678",
                        gender = validGender,
                        birthDate = validBirthDate,
                    )
                }
            }
        }
    }

    describe("사용자 탈퇴") {
        val user = UserFixture.createUser()

        context("사용자는 탈퇴할 수 있다.") {
            val withdrawTime = LocalDateTime.now()

            user.withdraw(withdrawTime)
        }

        context("이미 탈퇴한 경우엔 탈퇴할 수 없다.") {
            val withdrawTime = LocalDateTime.now()
            val withdrawnUser = user.withdraw(withdrawTime)

            shouldThrow<UserStatusException> {
                withdrawnUser.withdraw(withdrawTime)
            }
        }
    }

    describe("탈퇴 철회") {
        val user = UserFixture.createUser()

        context("탈퇴하지 않은 사용자가 탈퇴 철회할 수 없다.") {
            shouldThrow<UserStatusException> {
                user.revokeWithdrawal()
            }
        }

        context("탈퇴 후 ${User.WITHDRAWAL_REVOKE_DAYS}일 이내 탈퇴 철회할 수 있다.") {

            it("탈퇴 후 ${User.WITHDRAWAL_REVOKE_DAYS}일 이전") {
                val withdrawTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
                val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

                val revokeTime = withdrawTime.plusDays(User.WITHDRAWAL_REVOKE_DAYS - 1L)
                val revokedUser = withdrawnUser.revokeWithdrawal(revokeTime)

                revokedUser.isActive() shouldBe true
            }

            it("정확히 ${User.WITHDRAWAL_REVOKE_DAYS}일 째되는 날") {
                val withdrawTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
                val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

                val revokeTime = withdrawTime.plusDays(User.WITHDRAWAL_REVOKE_DAYS)
                val revokedUser = withdrawnUser.revokeWithdrawal(revokeTime)

                revokedUser.isActive() shouldBe true
            }

            it("같은 날에도 탈퇴를 철회할 수 있다.") {
                // 새벽 1시 탈퇴
                val withdrawTime = LocalDateTime.of(2024, 1, 1, 1, 0, 0)
                // 밤 11시 철회
                val revokeTime = LocalDateTime.of(2024, 1, 31, 23, 0, 0)
                val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

                val revokedUser = withdrawnUser.revokeWithdrawal(revokeTime)

                revokedUser.isActive() shouldBe true
            }
        }

        context("탈퇴 후 ${User.WITHDRAWAL_REVOKE_DAYS}일 지난 이후엔 탈퇴 철회할 수 없다.") {
            val withdrawTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0)
            val withdrawnUser = UserFixture.createWithdrawnUser(withdrawnAt = withdrawTime)

            val revokeTime = withdrawTime.plusDays(User.WITHDRAWAL_REVOKE_DAYS + 1L)

            shouldThrow<UserStatusException> {
                withdrawnUser.revokeWithdrawal(revokeTime)
            }
        }
    }
})
