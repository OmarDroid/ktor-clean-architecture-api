package com.omaroid.domain.usecases

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository

/**
 * Use case for creating new users in the system.
 *
 * Handles the business logic for user creation including validation,
 * duplicate checking, and data persistence. Ensures business rules
 * are enforced before persisting data.
 *
 * Business Rules:
 * - User name cannot be blank
 * - Email must be valid format (checked by Email value object)
 * - Email must be unique in the system
 * - User names are trimmed of whitespace
 *
 * @property userRepository Repository for user data operations
 *
 * @see UserRepository
 * @see User
 * @see Email
 */
class CreateUserUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Creates a new user with the provided email and name.
     *
     * Validates input data, checks for existing users with the same email,
     * and creates a new user record if all validation passes.
     *
     * @param email The user's email address (will be validated for format)
     * @param name The user's display name (will be trimmed and validated)
     * @return The newly created [User] entity with generated ID and timestamps
     *
     * @throws AppException.BadRequestException if name is blank or email format is invalid
     * @throws AppException.ConflictException if a user with the email already exists
     * @throws Exception if database operation fails
     */
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