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

fun Application.configureHealthRoutes() {
    val healthService by inject<HealthService>()

    routing {
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
