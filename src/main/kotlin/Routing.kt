package com.omaroid

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock.System.now

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        // Health check endpoint - simple version for Phase 1
        get("/health") {
            call.respond(
                HttpStatusCode.OK, mapOf(
                    "status" to "healthy",
                    "timestamp" to now().toString(),
                    "message" to "Ktor API is running"
                )
            )
        }
    }
}
