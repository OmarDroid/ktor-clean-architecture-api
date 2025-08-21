package com.omaroid.domain.errors

import io.ktor.http.HttpStatusCode

sealed class AppException(
    val statusCode: HttpStatusCode,
    override val message: String,
) : Exception(message) {

    class BadRequestException(message: String = "Invalid request") :
        AppException(HttpStatusCode.BadRequest, message)

    class NotFoundException(message: String = "Resource not found") :
        AppException(HttpStatusCode.NotFound, message)

    class ConflictException(message: String = "Resource conflict") :
        AppException(HttpStatusCode.Conflict, message)
}