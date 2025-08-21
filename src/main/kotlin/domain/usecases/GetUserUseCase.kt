package com.omaroid.domain.usecases

import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): User {
        val id = UserId(userId)
        return userRepository.findById(id)
            ?: throw AppException.NotFoundException("User with ID $userId not found")
    }
}