package com.cafe.payment.fixture

import com.cafe.payment.user.domain.Gender
import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.domain.WithdrawnUser
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicLong

object UserFixture {
    // 기본 테스트 데이터
    private var userId: AtomicLong = AtomicLong(1)

    fun generateUserId() = UserId(userId.incrementAndGet())

    private const val DEFAULT_NAME = "홍길동"
    private const val DEFAULT_PHONE_NUMBER = "01012345678"
    private val DEFAULT_GENDER = Gender.MALE
    private val DEFAULT_BIRTH_DATE = LocalDate.of(1990, 1, 1)
    private val DEFAULT_NOW = LocalDateTime.of(2024, 1, 1, 12, 0, 0)

    fun createUser(
        id: UserId = generateUserId(),
        name: String = DEFAULT_NAME,
        phoneNumber: String = DEFAULT_PHONE_NUMBER,
        gender: Gender = DEFAULT_GENDER,
        birthDate: LocalDate = DEFAULT_BIRTH_DATE,
        now: LocalDateTime = DEFAULT_NOW,
    ): User {
        return User.create(
            id = id,
            name = name,
            phoneNumber = phoneNumber,
            gender = gender,
            birthDate = birthDate,
            now = now,
        )
    }

    // 탈퇴 사용자
    fun createWithdrawnUser(
        id: UserId = generateUserId(),
        name: String = DEFAULT_NAME,
        phoneNumber: String = DEFAULT_PHONE_NUMBER,
        gender: Gender = DEFAULT_GENDER,
        birthDate: LocalDate = DEFAULT_BIRTH_DATE,
        createdAt: LocalDateTime = DEFAULT_NOW,
        withdrawnAt: LocalDateTime = DEFAULT_NOW.plusDays(1),
    ): WithdrawnUser {
        val user =
            createUser(
                id = id,
                name = name,
                phoneNumber = phoneNumber,
                gender = gender,
                birthDate = birthDate,
                now = createdAt,
            )
        return WithdrawnUser.create(user, withdrawnAt)
    }
}
