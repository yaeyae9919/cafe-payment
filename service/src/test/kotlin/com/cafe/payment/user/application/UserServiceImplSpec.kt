package com.cafe.payment.user.application

import com.cafe.payment.fixture.MockUserRepository
import com.cafe.payment.fixture.MockWithdrawnUserRepository
import com.cafe.payment.fixture.UserFixture
import com.cafe.payment.user.UserNotFoundException
import com.cafe.payment.user.UserRegisterException
import com.cafe.payment.user.domain.Gender
import com.cafe.payment.user.domain.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import java.time.LocalDate

class UserServiceImplSpec : DescribeSpec({

    val userRepository = MockUserRepository()
    val withdrawnUserRepository = MockWithdrawnUserRepository()
    val userService = UserServiceImpl(userRepository, withdrawnUserRepository)

    beforeEach {
        userRepository.clear()
        withdrawnUserRepository.clear()
    }

    describe("register") {
        context("이미 가입된 전화번호로 가입을 시도하는 경우 에러가 발생해요.") {
            // given
            val duplicatedPhoneNumber = "01012345678"
            val existingUser = UserFixture.createUser(phoneNumber = duplicatedPhoneNumber)
            userRepository.save(existingUser)

            val command =
                UserService.RegisterCommand(
                    name = "뉴 사용자",
                    // 동일한 전화번호로 가입 시도
                    phoneNumber = duplicatedPhoneNumber,
                    gender = Gender.FEMALE,
                    birthDate = LocalDate.of(1995, 5, 5),
                )

            // when & then
            shouldThrow<UserRegisterException> {
                userService.register(command)
            }
        }
    }

    describe("withdraw") {
        context("가입하지 않은 사용자를 탈퇴시킬 수 없어요.") {
            // given
            val nonExistentUserId = UserId(999L)

            // when & then
            shouldThrow<UserNotFoundException> {
                userService.withdraw(nonExistentUserId)
            }
        }
    }

    describe("revokeWithdrawal") {
        context("탈퇴하지 않은 사용자의 탈퇴를 철회할 수 없어요.") {
            // given
            val nonExistentUserId = UserId(999L)

            // when & then
            shouldThrow<UserNotFoundException> {
                userService.revokeWithdrawal(nonExistentUserId)
            }
        }
    }
})
