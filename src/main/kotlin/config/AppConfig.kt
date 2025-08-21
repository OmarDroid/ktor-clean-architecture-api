package com.omaroid.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig
import kotlin.text.isNotBlank
import kotlin.text.toInt

/**
 * Server configuration settings.
 *
 * Contains all server-related configuration parameters including
 * port binding and host interface settings.
 *
 * @property port The port number on which the server will listen (e.g., 8080)
 * @property host The host interface to bind to (e.g., "0.0.0.0" for all interfaces)
 */
data class ServerConfig(
    val port: Int,
    val host: String
)

/**
 * Database configuration settings with validation.
 *
 * Contains all database connection parameters with built-in validation
 * to ensure all required fields are provided and non-blank.
 *
 * @property url The JDBC database URL (e.g., "jdbc:postgresql://localhost:5432/mydb")
 * @property driver The JDBC driver class name (e.g., "org.postgresql.Driver")
 * @property user The database username for authentication
 * @property password The database password for authentication
 *
 * @throws IllegalArgumentException if any configuration value is blank
 */
data class DatabaseConfig(
    val url: String,
    val driver: String,
    val user: String,
    val password: String
) {
    init {
        require(url.isNotBlank()) { "Database URL cannot be blank" }
        require(driver.isNotBlank()) { "Database driver cannot be blank" }
        require(user.isNotBlank()) { "Database user cannot be blank" }
        require(password.isNotBlank()) { "Database password cannot be blank" }
    }
}

/**
 * Root application configuration container.
 *
 * Aggregates all configuration sections into a single, type-safe configuration object
 * that can be easily passed throughout the application via dependency injection.
 *
 * @property server Server configuration settings (port, host)
 * @property database Database connection configuration
 *
 * @see ServerConfig
 * @see DatabaseConfig
 */
data class AppConfig(
    val server: ServerConfig,
    val database: DatabaseConfig,
)

/**
 * Loads and validates application configuration from multiple sources.
 *
 * Configuration is loaded in the following priority order:
 * 1. Environment variables
 * 2. System properties
 * 3. HOCON configuration files (application.conf)
 *
 * This approach allows for flexible deployment scenarios while maintaining
 * type safety and validation.
 *
 * Required environment variables/configuration keys:
 * - `PORT` / `ktor.deployment.port`: Server port number
 * - `HOST` / `ktor.deployment.host`: Server host interface
 * - `DATABASE_URL` / `database.url`: Database JDBC URL
 * - `DATABASE_DRIVER` / `database.driver`: JDBC driver class
 * - `DATABASE_USER` / `database.user`: Database username
 * - `DATABASE_PASSWORD` / `database.password`: Database password
 *
 * @return A validated [AppConfig] instance containing all application settings
 * @throws IllegalStateException if required configuration is missing or invalid
 * @throws NumberFormatException if PORT cannot be parsed as an integer
 *
 * @see AppConfig
 * @see ServerConfig
 * @see DatabaseConfig
 */
fun loadConfig(): AppConfig {
    try {
        val config = HoconApplicationConfig(ConfigFactory.load())

        /**
         * Helper function to get configuration values from multiple sources.
         *
         * @param envKey The environment variable name
         * @param yamlPath The HOCON configuration path
         * @return The configuration value from the first available source
         * @throws IllegalStateException if the configuration key is not found in any source
         */
        fun getConfigValue(envKey: String, yamlPath: String): String {
            return System.getenv(envKey)
                ?: System.getProperty(envKey)
                ?: config.propertyOrNull(yamlPath)?.getString()
                ?: throw kotlin.IllegalStateException("Configuration '$envKey' is required")
        }
        val appConfig = AppConfig(
            server = ServerConfig(
                port = getConfigValue("PORT", "ktor.deployment.port").toInt(),
                host = getConfigValue("HOST", "ktor.deployment.host")
            ),
            database = DatabaseConfig(
                url = getConfigValue("DATABASE_URL", "database.url"),
                driver = getConfigValue("DATABASE_DRIVER", "database.driver"),
                user = getConfigValue("DATABASE_USER", "database.user"),
                password = getConfigValue("DATABASE_PASSWORD", "database.password")
            )
        )
        return appConfig
    } catch (e: Exception) {
        throw kotlin.IllegalStateException("Failed to load configuration", e)
    }
}