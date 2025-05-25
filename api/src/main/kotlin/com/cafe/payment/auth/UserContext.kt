package com.cafe.payment.auth

import com.cafe.payment.user.domain.UserId

/**
 * 서비스 사용이 가능한 사용자 ID를 담아둡니다.
 */
object UserContext {
    private val userIdThreadLocal = ThreadLocal<UserId>()

    fun setUserId(userId: UserId) {
        userIdThreadLocal.set(userId)
    }

    fun getCurrentUserId(): UserId {
        return userIdThreadLocal.get()
            ?: throw UserNotAuthenticatedException.userNotAuthenticated()
    }

    fun clear() {
        userIdThreadLocal.remove()
    }
}
