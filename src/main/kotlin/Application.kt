/**
 * Main application entry point for the Ktor Clean Architecture API.
 *
 * This package contains the core application setup and configuration,
 * implementing a clean architecture pattern with proper dependency injection
 * using Koin and database connectivity with Exposed ORM.
 */
package com.omaroid

import com.omaroid.config.AppConfig
import com.omaroid.config.loadConfig
import com.omaroid.di.createAppModule
import com.omaroid.presentation.plugins.configureStatusPages
import com.omaroid.presentation.routes.configureHealthRoutes
import com.omaroid.presentation.routes.configureRoutes
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * Main entry point for the Ktor application.
 *
 * Loads environment configuration, initializes the application config,
 * and starts the embedded server with the CIO engine.
 *
 * @see [loadConfig] for configuration loading details
 * @see [module] for application module configuration
 */
fun main() {
    // For local development, load .env into System Properties
    Dotenv.configure().filename("database.env")
        .ignoreIfMissing()
        .systemProperties()
        .load()
    // Load the unified, type-safe configuration
    val appConfig = loadConfig()
    // Start the server with the loaded configuration
    embeddedServer(
        factory = CIO,
        port = appConfig.server.port,
        host = appConfig.server.host,
        module = { module(appConfig) }
    ).start(wait = true)
}

/**
 * Configures the main Ktor application module with all necessary plugins and routing.
 *
 * Sets up:
 * - Koin dependency injection with application modules
 * - JSON content negotiation with kotlinx.serialization
 * - Global exception handling via status pages
 * - API routing for user management
 * - Health check endpoints for monitoring
 *
 * @param appConfig The application configuration containing server and database settings
 *
 * @see [createAppModule] for dependency injection setup
 * @see [configureStatusPages] for exception handling
 * @see [configureRoutes] for user API routes
 * @see [configureHealthRoutes] for health monitoring
 */
fun Application.module(appConfig: AppConfig) {
    // Configure Koin dependency injection
    install(Koin) {
        slf4jLogger()
        // Pass the config to Koin so it's available for injection throughout the app
        modules(createAppModule(appConfig))
        // This line is useful for allowing injection of the Application instance itself
        koin.declare(this@module)
    }

    // Configure content negotiation for JSON serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Configure application plugins and routing
    configureStatusPages()
    configureRoutes()
    configureHealthRoutes()
}