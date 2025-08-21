package com.omaroid.presentation.mappers

import com.omaroid.domain.entities.User
import com.omaroid.presentation.dto.PaginationDto
import com.omaroid.presentation.dto.UserResponseDto
import com.omaroid.presentation.dto.UsersPageResponseDto
import kotlin.math.ceil

object UserMapper {

    /**
     * Converts User domain entity to UserResponseDto
     */
    fun User.toDto(): UserResponseDto {
        return UserResponseDto(
            id = this.id.value,
            email = this.email.value,
            name = this.name,
            createdAt = this.createdAt.toString(),
            updatedAt = this.updatedAt.toString()
        )
    }

    /**
     * Converts list of Users to UsersPageResponseDto with proper pagination
     */
    fun List<User>.toPageDto(page: Int, size: Int, totalCount: Int): UsersPageResponseDto {
        val totalPages = if (totalCount == 0) 0 else ceil(totalCount.toDouble() / size).toInt()

        return UsersPageResponseDto(
            users = this.map { it.toDto() },
            pagination = PaginationDto(
                page = page,
                size = size,
                total = totalCount,
                totalPages = totalPages,
                hasNext = page < totalPages - 1,
                hasPrevious = page > 0
            )
        )
    }

    /**
     * Creates empty page response
     */
    fun createEmptyPage(page: Int, size: Int): UsersPageResponseDto {
        return UsersPageResponseDto(
            users = emptyList(),
            pagination = PaginationDto(
                page = page,
                size = size,
                total = 0,
                totalPages = 0,
                hasNext = false,
                hasPrevious = false
            )
        )
    }
}