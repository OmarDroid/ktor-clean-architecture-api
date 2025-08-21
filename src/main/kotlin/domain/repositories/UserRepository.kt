/**
 * Repository interfaces for data access abstraction.
 *
 * This package contains repository interfaces that define contracts for data access
 * operations, following the Repository pattern and dependency inversion principle
 * of clean architecture.
 */
package com.omaroid.domain.repositories

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId

/**
 * Repository interface for user data access operations.
 *
 * Defines the contract for all user-related data operations without coupling
 * to specific data storage implementations. This interface is implemented by
 * data layer components and used by domain use cases.
 *
 * All operations are suspending functions to support coroutine-based async operations.
 *
 * @see com.omaroid.data.repository.UserRepositoryImpl
 */
interface UserRepository {

    /**
     * Creates a new user with the specified email and name.
     *
     * @param email The user's email address (must be unique in the system)
     * @param name The user's display name
     * @return The newly created [User] entity with generated ID and timestamps
     * @throws Exception if email already exists or database operation fails
     */
    suspend fun create(email: Email, name: String): User

    /**
     * Finds a user by their unique identifier.
     *
     * @param id The unique user identifier to search for
     * @return The [User] entity if found, null otherwise
     */
    suspend fun findById(id: UserId): User?

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return The [User] entity if found, null otherwise
     */
    suspend fun findByEmail(email: Email): User?

    /**
     * Updates an existing user with new information.
     *
     * @param user The user entity with updated information (ID must exist)
     * @return The updated [User] entity with new updatedAt timestamp
     * @throws Exception if user ID doesn't exist or database operation fails
     */
    suspend fun update(user: User): User

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id The unique user identifier to delete
     * @return true if the user was successfully deleted, false if not found
     */
    suspend fun deleteById(id: UserId): Boolean

    /**
     * Retrieves a paginated list of all users.
     *
     * @param offset The number of records to skip (for pagination)
     * @param limit The maximum number of records to return (max 100)
     * @return A list of [User] entities ordered by ID ascending
     */
    suspend fun findAll(offset: Int = 0, limit: Int = 10): List<User>

    /**
     * Gets the total count of users in the system.
     *
     * Used for pagination calculations and system statistics.
     *
     * @return The total number of users in the database
     */
    suspend fun getTotalCount(): Int
}