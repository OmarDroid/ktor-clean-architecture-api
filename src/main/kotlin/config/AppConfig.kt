package com.omaroid.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.HoconApplicationConfig
import kotlin.text.isNotBlank
import kotlin.text.toInt

data class ServerConfig(
    val port: Int,
    val host: String
)

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

data class AppConfig(
    val server: ServerConfig,
    val database: DatabaseConfig,
)

fun loadConfig(): AppConfig {
    try {
        val config = HoconApplicationConfig(ConfigFactory.load())
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