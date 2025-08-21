package com.omaroid.domain.usecases

import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository

/**
 * Use case for deleting users from the system.
 *
 * Handles the business logic for user deletion including existence validation
 * and data removal. Ensures that only existing users can be deleted.
 *
 * Business Rules:
 * - User must exist before deletion
 * - Deletion is permanent and cannot be undone
 * - All user data is removed from the system
 *
 * @property userRepository Repository for user data operations
 *
 * @see UserRepository
 * @see UserId
 */
class DeleteUserUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Deletes a user from the system by their unique identifier.
     *
     * Validates that the user exists before attempting deletion.
     * Once deleted, the user data is permanently removed from the system.
     *
     * @param userId The unique identifier of the user to delete
     * @return true if the user was successfully deleted
     *
     * @throws IllegalArgumentException if userId is not positive (via UserId validation)
     * @throws AppException.NotFoundException if no user exists with the given ID
     * @throws Exception if database operation fails
     */
    suspend operator fun invoke(userId: Long): Boolean {
        val id = UserId(userId)
        userRepository.findById(id)
            ?: throw AppException.NotFoundException("User with ID $userId not found")
        return userRepository.deleteById(id)
    }
}