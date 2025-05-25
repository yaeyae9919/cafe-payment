package com.cafe.payment.user.application

import com.cafe.payment.library.generator.LongIdGenerator
import com.cafe.payment.user.domain.User
import com.cafe.payment.user.domain.UserId
import com.cafe.payment.user.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
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
}
