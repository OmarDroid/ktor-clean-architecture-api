package com.omaroid.presentation.mappers

import com.omaroid.domain.entities.User
import com.omaroid.presentation.dto.PaginationDto
import com.omaroid.presentation.dto.UserResponseDto
import com.omaroid.presentation.dto.UsersPageResponseDto
import kotlin.math.ceil

object UserMapper {

    /**
     * Converts User domain entity to UserResponseDto.
     *
     * Maps all user properties to their corresponding DTO representation,
     * converting timestamps to ISO-8601 string format and extracting
     * primitive values from value objects.
     *
     * @receiver The User domain entity to convert
     * @return UserResponseDto suitable for JSON serialization
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
     * Converts list of Users to UsersPageResponseDto with proper pagination.
     *
     * Creates a complete paginated response including user data and pagination
     * metadata. Calculates total pages, navigation flags, and other pagination
     * information based on the provided parameters.
     *
     * @receiver List of User entities for the current page
     * @param page Current page number (0-based)
     * @param size Number of items per page
     * @param totalCount Total number of users across all pages
     * @return UsersPageResponseDto with users and pagination metadata
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
     * Creates empty page response for queries with no results.
     *
     * Generates a proper paginated response structure when no users are found,
     * ensuring consistent API response format even for empty result sets.
     *
     * @param page Current page number (0-based)
     * @param size Number of items per page
     * @return UsersPageResponseDto with empty user list and appropriate pagination metadata
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