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

class UserController(
    private val createUserUseCase: CreateUserUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase,
) {

    suspend fun createUser(call: ApplicationCall) {
        val request = call.receive<CreateUserRequestDto>()
        val user = createUserUseCase(request.email, request.name)
        call.respond(
            HttpStatusCode.Companion.Created, createSuccessResponse(user.toDto())
        )
    }

    suspend fun getUserById(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toLongOrNull()
            ?: throw AppException.BadRequestException("User ID must be a valid number")

        val user = getUserUseCase(userId)
        call.respond(
            HttpStatusCode.Companion.OK, createSuccessResponse(user.toDto())
        )
    }

    suspend fun updateUser(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toLongOrNull()
            ?: throw AppException.BadRequestException("User ID must be a valid number")

        val request = call.receive<UpdateUserRequestDto>()
        val user = updateUserUseCase(userId, request.name)

        call.respond(
            HttpStatusCode.Companion.OK, createSuccessResponse(user.toDto())
        )
    }

    suspend fun deleteUser(call: ApplicationCall) {
        val userId = call.parameters["id"]?.toLongOrNull()
            ?: throw AppException.BadRequestException("User ID must be a valid number")

        deleteUserUseCase(userId)
        call.respond(HttpStatusCode.Companion.NoContent)
    }

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

    private fun <T> createSuccessResponse(data: T): ApiResponse<T> {
        return ApiResponse(
            success = true, data = data, timestamp = Clock.System.now().toString()
        )
    }
}