/**
 * Ktor plugins for cross-cutting concerns.
 *
 * This package contains plugin configurations for Ktor framework features
 * like exception handling, content negotiation, and other middleware
 * functionalities that span across the application.
 */
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

/**
 * Simple error response structure for consistent error formatting.
 *
 * @property message Human-readable error message describing what went wrong
 */
@Serializable
data class ErrorResponse(val message: String)

/**
 * Configures global exception handling for the application.
 *
 * Sets up the StatusPages plugin to handle various types of exceptions
 * and convert them to appropriate HTTP responses with consistent error
 * formatting. Provides graceful error handling for both expected
 * application exceptions and unexpected system errors.
 *
 * Exception Handling:
 * - AppException: Domain-specific errors with predefined HTTP status codes
 * - IllegalArgumentException: Validation errors from domain objects (400 Bad Request)
 * - BadRequestException: Ktor request parsing errors (400 Bad Request)
 * - SerializationException: JSON parsing/serialization errors (400 Bad Request)
 * - Throwable: Catch-all for unexpected errors (500 Internal Server Error)
 *
 * @receiver Application The Ktor application instance to configure
 *
 * @see AppException
 * @see StatusPages
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        /**
         * Handle application-specific exceptions with predefined status codes.
         *
         * Maps domain exceptions to their corresponding HTTP status codes
         * as defined in the exception classes.
         */
        exception<AppException> { call, cause ->
            call.respond(
                status = cause.statusCode, message = ErrorResponse(message = cause.message)
            )
        }

        /**
         * Handle DTO validation errors from init blocks.
         *
         * Catches IllegalArgumentExceptions thrown by domain value objects
         * during validation and converts them to Bad Request responses.
         */
        exception<IllegalArgumentException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(message = cause.message ?: "Invalid request data")
            )
        }

        /**
         * Handle content transformation and JSON parsing errors.
         *
         * Catches BadRequestExceptions from Ktor's request processing
         * when content cannot be parsed or transformed properly.
         */
        exception<BadRequestException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(message = "Invalid request format")
            )
        }

        /**
         * Handle JSON deserialization errors.
         *
         * Catches SerializationExceptions from kotlinx.serialization
         * when JSON cannot be parsed into the expected data structures.
         */
        exception<SerializationException> { call, cause ->
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse(message = "Invalid JSON format: ${cause.message}")
            )
        }

        /**
         * Handle all other unexpected errors.
         *
         * Catch-all handler for any unhandled exceptions to prevent
         * the application from crashing and provide a consistent error response.
         * Logs the full exception details while returning a generic error message.
         */
        exception<Throwable> { call, cause ->
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse(message = "Internal Server Error: ${cause.localizedMessage}")
            )
        }
    }
}