package com.omaroid.domain.usecases

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository

class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, name: String): User {
        // Validate input
        if (name.isBlank()) {
            throw AppException.BadRequestException("Name cannot be blank")
        }

        val emailValue = try {
            Email(email)
        } catch (e: IllegalArgumentException) {
            throw AppException.BadRequestException(e.message ?: "Invalid email format")
        }

        // Check if user already exists
        if (userRepository.findByEmail(emailValue) != null) {
            throw AppException.ConflictException("User with email $email already exists")
        }

        // Create user
        return userRepository.create(emailValue, name.trim())
    }
}