package com.omaroid.data.repository

import com.omaroid.data.database.tables.UsersTable
import com.omaroid.data.database.tables.UsersTable.createdAt
import com.omaroid.data.database.tables.UsersTable.email
import com.omaroid.data.database.tables.UsersTable.name
import com.omaroid.data.database.tables.UsersTable.updatedAt
import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock.System.now
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

/**
 * Exposed ORM implementation of the UserRepository interface.
 *
 * Provides database persistence for user entities using the Exposed ORM framework.
 * All database operations are executed within suspended transactions on the IO
 * dispatcher to ensure non-blocking coroutine-based async operations.
 *
 * Key Features:
 * - Automatic timestamp management (createdAt/updatedAt)
 * - Safe type conversion between database rows and domain entities
 * - Proper transaction handling with error propagation
 * - Optimized queries with appropriate indexing
 *
 * @property database The database instance for executing SQL operations
 *
 * @see UserRepository
 * @see UsersTable
 * @see User
 */
class UserRepositoryImpl(private val database: Database) : UserRepository {

    /**
     * Creates a new user record in the database.
     *
     * Inserts a new user with the provided email and name, automatically setting
     * creation and update timestamps. Returns the complete user entity with the
     * generated database ID.
     *
     * @param email The user's validated email address
     * @param name The user's display name (already trimmed by use case)
     * @return The newly created [User] entity with generated ID and timestamps
     * @throws Exception if database operation fails or email constraint is violated
     */
    override suspend fun create(email: Email, name: String): User = dbQuery {
        val now = now()

        val insertResult = UsersTable.insert {
            it[UsersTable.email] = email.value
            it[UsersTable.name] = name
            it[createdAt] = now
            it[updatedAt] = now
        }

        val id = insertResult[UsersTable.id]

        User(
            id = UserId(id.value), email = email, name = name, createdAt = now, updatedAt = now
        )
    }

    /**
     * Finds a user by their unique identifier.
     *
     * Executes an indexed lookup by primary key for optimal performance.
     *
     * @param id The user's unique identifier
     * @return The [User] entity if found, null otherwise
     */
    override suspend fun findById(id: UserId): User? = dbQuery {
        UsersTable.select { UsersTable.id eq id.value }.singleOrNull()?.toUser()
    }

    /**
     * Finds a user by their email address.
     *
     * Executes an indexed lookup using the unique email index for optimal performance.
     *
     * @param email The user's email address
     * @return The [User] entity if found, null otherwise
     */
    override suspend fun findByEmail(email: Email): User? = dbQuery {
        UsersTable.select { UsersTable.email eq email.value }.singleOrNull()?.toUser()
    }

    /**
     * Updates an existing user record with new information.
     *
     * Updates the user's data and automatically sets the updatedAt timestamp
     * to the current time. Only modifiable fields are updated.
     *
     * @param user The user entity with updated information
     * @return The updated [User] entity with new updatedAt timestamp
     * @throws Exception if user ID doesn't exist or database operation fails
     */
    override suspend fun update(user: User): User = dbQuery {
        val now = now()

        UsersTable.update({ UsersTable.id eq user.id.value }) {
            it[email] = user.email.value
            it[name] = user.name
            it[updatedAt] = now
        }

        user.copy(updatedAt = now)
    }

    /**
     * Deletes a user record from the database.
     *
     * Permanently removes the user record. This operation cannot be undone.
     *
     * @param id The unique identifier of the user to delete
     * @return true if a record was deleted, false if no record found
     */
    override suspend fun deleteById(id: UserId): Boolean = dbQuery {
        UsersTable.deleteWhere { UsersTable.id eq id.value } > 0
    }

    /**
     * Retrieves a paginated list of users ordered by ID.
     *
     * Fetches users in ascending ID order for consistent pagination.
     * Uses LIMIT and OFFSET for efficient database-level pagination.
     *
     * @param offset The number of records to skip
     * @param limit The maximum number of records to return
     * @return List of [User] entities for the requested page
     */
    override suspend fun findAll(offset: Int, limit: Int): List<User> = dbQuery {
        UsersTable.selectAll().orderBy(UsersTable.id to SortOrder.ASC).limit(limit, offset.toLong())
            .map { it.toUser() }
    }

    /**
     * Gets the total count of users in the database.
     *
     * Executes a COUNT query to determine the total number of user records.
     * Used for pagination calculations and system metrics.
     *
     * @return The total number of users in the system
     */
    override suspend fun getTotalCount(): Int = dbQuery {
        UsersTable.selectAll().count().toInt()
    }

    /**
     * Executes database operations within a suspended transaction.
     *
     * Provides a consistent way to execute database operations asynchronously
     * using the IO dispatcher to avoid blocking the calling thread.
     *
     * @param T The return type of the database operation
     * @param block The database operation to execute
     * @return The result of the database operation
     */
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    /**
     * Converts a database result row to a User domain entity.
     *
     * Handles the mapping between database columns and domain entity properties,
     * including proper type conversion and value object creation.
     *
     * @return A [User] entity constructed from the database row
     */
    private fun ResultRow.toUser(): User = User(
        id = UserId(this[UsersTable.id].value),
        email = Email(this[email]),
        name = this[name],
        createdAt = this[createdAt],
        updatedAt = this[updatedAt]
    )
}