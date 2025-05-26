package com.cafe.payment.user.application

import com.cafe.payment.library.generator.LongIdGenerator
import com.cafe.payment.user.UserNotFoundException
import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.domain.WithdrawnUser
import com.cafe.payment.user.repository.UserRepository
import com.cafe.payment.user.repository.WithdrawnUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val withDrawnUserRepository: WithdrawnUserRepository,
) : UserService {
    @Transactional(readOnly = true)
    override fun isActive(userId: UserId): Boolean {
        val user = userRepository.findById(userId)
        return user != null
    }

    @Transactional(readOnly = true)
    override fun isRevokableWithdrawn(
        userId: UserId,
        now: LocalDateTime,
    ): Boolean {
        val withdrawnUser = withDrawnUserRepository.findById(userId) ?: return false
        return !withdrawnUser.cannotRevokable(now)
    }

    @Transactional
    override fun register(command: UserService.RegisterCommand): UserId {
        val now = LocalDateTime.now()

        val user =
            User.create(
                id = UserId(LongIdGenerator.generate()),
                name = command.name,
                phoneNumber = command.phoneNumber,
                gender = command.gender,
                birthDate = command.birthDate,
                now = now,
            )

        return userRepository.save(user).id
    }

    @Transactional
    override fun withdraw(userId: UserId): UserId {
        val now = LocalDateTime.now()

        val user = userRepository.findById(userId) ?: throw UserNotFoundException.notFoundActiveUser()
        // 활성 유저 데이터에서 hard-delete 처리
        userRepository.deleteById(user.id)

        // 탈퇴 유저 데이터로 분리 보관
        val withdrawn = WithdrawnUser.create(user, now)
        return withDrawnUserRepository.save(withdrawn).userId
    }

    // 탈퇴 철회 시 즉시 서비스 사용이 가능해야햔다.
    @Transactional
    override fun revokeWithdrawal(userId: UserId): UserId {
        val now = LocalDateTime.now()
        val withdrawnUser = withDrawnUserRepository.findById(userId) ?: throw UserNotFoundException.notFoundWithdrawnUser()

        // 탈퇴 유저 데이터 hard-delete
        withDrawnUserRepository.deleteById(withdrawnUser.userId)
        // 액티브 유저 저장
        val user = withdrawnUser.toRevokedUser(now)
        return userRepository.save(user).id
    }
}
