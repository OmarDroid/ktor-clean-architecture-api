package com.omaroid.presentation.routes

import com.omaroid.domain.repositories.HealthService
import com.omaroid.presentation.dto.HealthStatus
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingContext
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.datetime.Clock
import org.koin.ktor.ext.inject

/**
 * Configures health monitoring routes for the application.
 *
 * Sets up health check endpoints used by monitoring systems, load balancers,
 * and operations teams to verify system availability and component status.
 * Provides detailed information about database connectivity and overall
 * system health.
 *
 * Configured Routes:
 * - GET `/health` - System health check with component status
 *
 * @receiver Application The Ktor application instance to configure
 *
 * @see HealthService
 * @see HealthStatus
 */
fun Application.configureHealthRoutes() {
    val healthService by inject<HealthService>()

    routing {
        /**
         * System health check endpoint.
         *
         * Performs comprehensive health checks on critical system components
         * and returns detailed status information. Used by monitoring systems
         * and load balancers to determine service availability.
         *
         * GET /health
         *
         * Response Codes:
         * - 200 OK: All systems healthy and operational
         * - 503 Service Unavailable: One or more critical components unhealthy
         *
         * Response Body: HealthStatus JSON with:
         * - status: "healthy" or "unhealthy"
         * - timestamp: ISO-8601 timestamp of the check
         * - database: "connected" or "disconnected"
         * - version: Application version
         * - error: Optional error message when unhealthy
         */
        get("/health") {
            val healthy = healthService.checkHealth()
            val status = HealthStatus(
                status = if (healthy) "healthy" else "unhealthy",
                timestamp = Clock.System.now().toString(),
                database = if (healthy) "connected" else "disconnected"
            )
            val code = if (healthy) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable
            call.respond(code, status)
        }
    }
}
