package com.omaroid.domain.usecases

import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository

/**
 * Use case for retrieving a single user by their ID.
 *
 * Handles the business logic for fetching individual users from the system.
 * Ensures proper error handling when users are not found.
 *
 * @property userRepository Repository for user data operations
 *
 * @see UserRepository
 * @see User
 * @see UserId
 */
class GetUserUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Retrieves a user by their unique identifier.
     *
     * Validates the user ID and fetches the corresponding user from the repository.
     * Throws a not found exception if the user doesn't exist.
     *
     * @param userId The unique identifier of the user to retrieve
     * @return The [User] entity if found
     *
     * @throws IllegalArgumentException if userId is not positive (via UserId validation)
     * @throws AppException.NotFoundException if no user exists with the given ID
     */
    suspend operator fun invoke(userId: Long): User {
        val id = UserId(userId)
        return userRepository.findById(id)
            ?: throw AppException.NotFoundException("User with ID $userId not found")
    }
}