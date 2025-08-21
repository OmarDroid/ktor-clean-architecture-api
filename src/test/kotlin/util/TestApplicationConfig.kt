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

    fun Application.setupTestApplication() {
        install(ServerContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        // Koin context is managed by KoinTestRule
        configureStatusPages()
        configureRoutes()
    }

    fun ApplicationTestBuilder.createConfiguredClient(): HttpClient {
        return createClient {
            install(ClientContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
}