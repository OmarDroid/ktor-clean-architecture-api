package com.omaroid.domain.usecases

import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository

class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: Long): Boolean {
        val id = UserId(userId)
        userRepository.findById(id)
            ?: throw AppException.NotFoundException("User with ID $userId not found")
        return userRepository.deleteById(id)
    }
}