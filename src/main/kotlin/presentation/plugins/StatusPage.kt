package com.omaroid.presentation.plugins

import com.omaroid.domain.errors.AppException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
data class ErrorResponse(val message: String)

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AppException> { call, cause ->
            call.respond(
                status = cause.statusCode, message = ErrorResponse(message = cause.message)
            )
        }

        // Handle DTO validation errors from init blocks
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(message = cause.message ?: "Invalid request data")
            )
        }

        // Handle content transformation and JSON parsing errors
        exception<BadRequestException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(message = "Invalid request format")
            )
        }

        // Handle JSON deserialization errors
        exception<SerializationException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(message = "Invalid JSON format: ${cause.message}")
            )
        }

        // Handle Internal Server Error
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(message = "Internal Server Error: ${cause.localizedMessage}")
            )
        }
    }
}