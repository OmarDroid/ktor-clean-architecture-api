package com.omaroid.domain.usecases

import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository

class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long, name: String): User {
        if (name.isBlank()) {
            throw AppException.BadRequestException("Name cannot be blank")
        }

        val id = UserId(userId)
        val existingUser = userRepository.findById(id)
            ?: throw AppException.NotFoundException("User with ID $userId not found")

        val updatedUser = existingUser.copy(
            name = name.trim()
        )

        return userRepository.update(updatedUser)
    }
}