package com.omaroid.domain.usecases

import com.omaroid.domain.entities.User
import com.omaroid.domain.repositories.UserRepository

/**
 * Use case for retrieving paginated lists of users from the system.
 *
 * Handles the business logic for fetching users with pagination support,
 * including validation of pagination parameters and total count calculation
 * for proper pagination metadata.
 *
 * Business Rules:
 * - Page number must be non-negative (0-based indexing)
 * - Page size must be positive and not exceed 100
 * - Results are ordered by user ID in ascending order
 * - Returns both user list and total count for pagination
 *
 * @property userRepository Repository for user data operations
 *
 * @see UserRepository
 * @see User
 */
class GetAllUsersUseCase(
    private val userRepository: UserRepository
) {
    /**
     * Retrieves a paginated list of users from the system.
     *
     * Validates pagination parameters and fetches the requested page of users
     * along with the total count for proper pagination metadata calculation.
     *
     * @param page The page number to retrieve (0-based, default: 0)
     * @param size The number of users per page (default: 10, max: 100)
     * @return A [Pair] containing the list of users for the requested page and the total user count
     *
     * @throws IllegalArgumentException if page is negative, size is not positive, or size exceeds 100
     * @throws Exception if database operation fails
     */
    suspend operator fun invoke(page: Int = 0, size: Int = 10): Pair<List<User>, Int> {
        require(page >= 0) { "Page must be non-negative" }
        require(size > 0) { "Size must be positive" }
        require(size <= 100) { "Size cannot exceed 100" }

        val offset = page * size
        val users = userRepository.findAll(offset, size)
        val totalCount = userRepository.getTotalCount()
        return Pair(users, totalCount)
    }
}