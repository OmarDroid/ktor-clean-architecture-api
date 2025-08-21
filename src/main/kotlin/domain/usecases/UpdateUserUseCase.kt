package com.omaroid.domain.usecases

import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository

/**
 * Use case for updating existing users in the system.
 *
 * Handles the business logic for user updates including validation,
 * existence checking, and data persistence. Currently supports updating
 * the user's display name while preserving other attributes.
 *
 * Business Rules:
 * - User must exist in the system
 * - Name cannot be blank
 * - Names are trimmed of whitespace
 * - Email and ID cannot be changed (immutable)
 *
 * @property userRepository Repository for user data operations
 *
 * @see UserRepository
 * @see User
 * @see UserId
 */
class UpdateUserUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Updates a user's display name.
     *
     * Validates input data, ensures the user exists, and updates the user's
     * name while preserving all other attributes. The updatedAt timestamp
     * is automatically set to the current time.
     *
     * @param userId The unique identifier of the user to update
     * @param name The new display name for the user (will be trimmed)
     * @return The updated [User] entity with new name and updatedAt timestamp
     *
     * @throws IllegalArgumentException if userId is not positive (via UserId validation)
     * @throws AppException.BadRequestException if name is blank
     * @throws AppException.NotFoundException if no user exists with the given ID
     * @throws Exception if database operation fails
     */
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