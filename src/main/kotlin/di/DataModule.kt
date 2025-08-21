package com.omaroid.di

import com.omaroid.config.DatabaseConfig
import com.omaroid.data.database.tables.UsersTable
import com.omaroid.data.health.DatabaseHealthService
import com.omaroid.data.repository.UserRepositoryImpl
import com.omaroid.domain.repositories.HealthService
import com.omaroid.domain.repositories.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module

/**
 * Dependency injection module for the data layer.
 *
 * Configures all data layer components including database connections,
 * repository implementations, and health services. Handles database
 * initialization and schema creation for development environments.
 *
 * Configured Dependencies:
 * - Database: Exposed ORM database connection
 * - UserRepository: Repository implementation for user data access
 * - HealthService: Database health monitoring service
 *
 * @see DatabaseConfig
 * @see UserRepositoryImpl
 * @see DatabaseHealthService
 */
val dataModule = module {

    /**
     * Provides a configured database connection with schema initialization.
     *
     * Creates an Exposed database connection using the provided configuration
     * and automatically creates tables in development mode. In production,
     * this should be replaced with proper database migration tools.
     *
     * @throws IllegalStateException if database connection fails
     */
    single<Database> {
        val dbConfig = get<DatabaseConfig>() // Get the config from Koin

        try {
            Database.connect(
                url = dbConfig.url,
                driver = dbConfig.driver,
                user = dbConfig.user,
                password = dbConfig.password
            ).also { database ->
                // NOTE: In production, replace this with proper database migrations
                // This is only for development/demo purposes
                transaction(database) {
                    SchemaUtils.create(UsersTable)
                }
                println("Database connected successfully: ${dbConfig.url}")
            }
        } catch (e: Exception) {
            println("Failed to connect to database: ${e.message}")
            throw IllegalStateException("Database connection failed", e)
        }
    }

    /**
     * Provides the user repository implementation.
     *
     * Configures the UserRepositoryImpl with the database connection
     * for data persistence operations.
     */
    single<UserRepository> { UserRepositoryImpl(get()) }

    /**
     * Provides the database health monitoring service.
     *
     * Configures the DatabaseHealthService with the database connection
     * for health check operations.
     */
    single<HealthService> { DatabaseHealthService(get()) }
}