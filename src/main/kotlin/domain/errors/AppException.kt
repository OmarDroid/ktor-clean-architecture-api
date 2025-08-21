/**
 * Application-specific exceptions for domain error handling.
 *
 * This package contains the custom exception hierarchy that maps domain errors
 * to appropriate HTTP status codes for RESTful API responses, following
 * clean architecture principles.
 */
package com.omaroid.domain.errors

import io.ktor.http.HttpStatusCode

/**
 * Base sealed class for all application-specific exceptions.
 *
 * Provides a consistent way to handle domain errors with appropriate HTTP status codes.
 * All application exceptions extend this base class to ensure proper error handling
 * and consistent API responses.
 *
 * @property statusCode The HTTP status code to return when this exception occurs
 * @property message Human-readable error message describing what went wrong
 *
 * @see BadRequestException
 * @see NotFoundException
 * @see ConflictException
 */
sealed class AppException(
    val statusCode: HttpStatusCode,
    override val message: String,
) : Exception(message) {

    /**
     * Exception for client errors indicating invalid or malformed requests.
     *
     * Used when the client sends invalid data, missing required fields,
     * or violates business rules. Maps to HTTP 400 Bad Request.
     *
     * @param message Descriptive error message explaining what was invalid
     */
    class BadRequestException(message: String = "Invalid request") :
        AppException(HttpStatusCode.BadRequest, message)

    /**
     * Exception for cases where a requested resource cannot be found.
     *
     * Used when querying for entities that don't exist in the system.
     * Maps to HTTP 404 Not Found.
     *
     * @param message Descriptive error message indicating what resource was not found
     */
    class NotFoundException(message: String = "Resource not found") :
        AppException(HttpStatusCode.NotFound, message)

    /**
     * Exception for cases where a request conflicts with the current system state.
     *
     * Used when attempting operations that would violate business constraints,
     * such as creating duplicate resources. Maps to HTTP 409 Conflict.
     *
     * @param message Descriptive error message explaining the conflict
     */
    class ConflictException(message: String = "Resource conflict") :
        AppException(HttpStatusCode.Conflict, message)
}