package com.cafe.payment.user.domain

import com.cafe.payment.user.InvalidUserDataException
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
})
