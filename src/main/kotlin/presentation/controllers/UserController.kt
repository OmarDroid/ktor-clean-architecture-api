package com.omaroid.presentation.controllers

import com.omaroid.domain.errors.AppException
import com.omaroid.domain.usecases.CreateUserUseCase
import com.omaroid.domain.usecases.DeleteUserUseCase
import com.omaroid.domain.usecases.GetAllUsersUseCase
import com.omaroid.domain.usecases.GetUserUseCase
import com.omaroid.domain.usecases.UpdateUserUseCase
import com.omaroid.presentation.dto.ApiResponse
import com.omaroid.presentation.dto.CreateUserRequestDto
import com.omaroid.presentation.dto.UpdateUserRequestDto
import com.omaroid.presentation.mappers.UserMapper
import com.omaroid.presentation.mappers.UserMapper.toDto
import com.omaroid.presentation.mappers.UserMapper.toPageDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import kotlinx.datetime.Clock

/**
 * Controller handling HTTP requests for user management operations.
 *
 * Orchestrates user-related HTTP requests by coordinating with domain use cases,
 * handling request/response parsing, validation, and proper HTTP status code
 * assignment. Provides a clean separation between HTTP concerns and business logic.
 *
 * @property createUserUseCase Use case for creating new users
 * @property getUserUseCase Use case for retrieving individual users
 * @property updateUserUseCase Use case for updating existing users
 * @property deleteUserUseCase Use case for deleting users
 * @property getAllUsersUseCase Use case for retrieving paginated user lists
 *
 * @see CreateUserUseCase
 * @see GetUserUseCase
 * @see UpdateUserUseCase
 * @see DeleteUserUseCase
 * @see GetAllUsersUseCase
 */
class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
) {

    /**
     * Handles HTTP POST requests to create new users.
     *
     * Parses the JSON request body, validates the data through the use case,
     * and returns the created user with HTTP 201 Created status.
     *
     * @param call The Ktor ApplicationCall containing request/response context
     *
     * @throws AppException.BadRequestException if request data is invalid
     * @throws AppException.ConflictException if user email already exists
     */
    suspend fun createUser(call: ApplicationCall) {
        val request = call.receive<CreateUserRequestDto>()
        val user = createUserUseCase(request.email, request.name)
        call.respond(
            HttpStatusCode.Companion.Created, createSuccessResponse(user.toDto())
        )
    }

    /**
     * Handles HTTP GET requests to retrieve a user by ID.
     *
     * Extracts the user ID from the URL path parameter, validates it,
     * and returns the user data with HTTP 200 OK status.
     *
     * @param call The Ktor ApplicationCall containing request/response context
     *
     * @throws AppException.BadRequestException if user ID is invalid
     * @throws AppException.NotFoundException if user doesn't exist
     */
    suspend fun getUserById(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toLongOrNull()
            ?: throw AppException.BadRequestException("User ID must be a valid number")

        val user = getUserUseCase(userId)
        call.respond(
            HttpStatusCode.Companion.OK, createSuccessResponse(user.toDto())
        )
    }

    /**
     * Handles HTTP PUT requests to update existing users.
     *
     * Extracts the user ID from the URL path parameter, parses the JSON
     * request body, and updates the user with HTTP 200 OK status.
     *
     * @param call The Ktor ApplicationCall containing request/response context
     *
     * @throws AppException.BadRequestException if user ID is invalid or request data is invalid
     * @throws AppException.NotFoundException if user doesn't exist
     */
    suspend fun updateUser(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toLongOrNull()
            ?: throw AppException.BadRequestException("User ID must be a valid number")

        val request = call.receive<UpdateUserRequestDto>()
        val user = updateUserUseCase(userId, request.name)

        call.respond(
            HttpStatusCode.Companion.OK, createSuccessResponse(user.toDto())
        )
    }

    /**
     * Handles HTTP DELETE requests to remove users from the system.
     *
     * Extracts the user ID from the URL path parameter, validates it,
     * and deletes the user with HTTP 204 No Content status.
     *
     * @param call The Ktor ApplicationCall containing request/response context
     *
     * @throws AppException.BadRequestException if user ID is invalid
     * @throws AppException.NotFoundException if user doesn't exist
     */
    suspend fun deleteUser(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toLongOrNull()
            ?: throw AppException.BadRequestException("User ID must be a valid number")

        deleteUserUseCase(userId)
        call.respond(HttpStatusCode.Companion.NoContent)
    }

    /**
     * Handles HTTP GET requests for paginated user listings.
     *
     * Parses optional query parameters for pagination (page and size),
     * applies defaults and validation, and returns the paginated results
     * with HTTP 200 OK status.
     *
     * Query Parameters:
     * - page: Page number (0-based, default: 0)
     * - size: Page size (1-100, default: 10)
     *
     * @param call The Ktor ApplicationCall containing request/response context
     */
    suspend fun getAllUsers(call: ApplicationCall) {
        val page = call.request.queryParameters["page"]?.toIntOrNull()?.takeIf { it >= 0 } ?: 0
        val size =
            call.request.queryParameters["size"]?.toIntOrNull()?.takeIf { it in 1..100 } ?: 10

        val (users, totalCount) = getAllUsersUseCase(page, size)

        val response = if (users.isEmpty()) {
            UserMapper.createEmptyPage(page, size)
        } else {
            users.toPageDto(page, size, totalCount)
        }
        call.respond(HttpStatusCode.Companion.OK, createSuccessResponse(response))
    }

    /**
     * Creates a standardized success response wrapper.
     *
     * Generates a consistent ApiResponse structure for successful operations
     * with the current timestamp for tracking purposes.
     *
     * @param T The type of the response data
     * @param data The payload to include in the response
     * @return ApiResponse wrapper with success status and timestamp
     */
    private fun <T> createSuccessResponse(data: T): ApiResponse<T> {
        return ApiResponse(
            success = true, data = data, timestamp = Clock.System.now().toString()
        )
    }
}