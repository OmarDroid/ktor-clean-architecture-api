package util

import com.omaroid.presentation.plugins.configureStatusPages
import com.omaroid.presentation.routes.configureRoutes
import io.ktor.client.HttpClient
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.testing.ApplicationTestBuilder
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation

/**
 * Configuration utility for Ktor test applications.
 */
object TestApplicationConfig {

    /**
     * Sets up a test application with standard configuration.
     *
     * Configures the test application with:
     * - JSON content negotiation for request/response serialization
     * - Status pages for exception handling
     * - User management routes
     *
     * Note: Koin context management is handled externally by KoinTestRule
     * to allow for proper dependency injection setup in test environments.
     *
     * @receiver Application The test application instance to configure
     *
     * @see configureStatusPages
     * @see configureRoutes
     */
    fun Application.setupTestApplication() {
        install(ServerContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        // Koin context is managed by KoinTestRule
        configureStatusPages()
        configureRoutes()
    }

    /**
     * Creates a configured HTTP client for testing API endpoints.
     *
     * Configures an HTTP client with:
     * - JSON content negotiation matching the server configuration
     * - Proper serialization settings for test requests/responses
     *
     * This client can be used to make HTTP requests to the test application
     * and verify responses in integration tests.
     *
     * @receiver ApplicationTestBuilder The test builder context
     * @return Configured HttpClient for making test requests
     */
    fun ApplicationTestBuilder.createConfiguredClient(): HttpClient {
        return createClient {
            install(ClientContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
}