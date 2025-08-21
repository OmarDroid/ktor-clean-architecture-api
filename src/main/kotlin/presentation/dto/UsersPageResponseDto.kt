package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for paginated user list responses.
 *
 * Combines user data with pagination metadata to provide complete
 * information for paginated user listings. Used by the GET /users
 * endpoint to return structured, paginated results.
 *
 * @property users List of user data for the current page
 * @property pagination Metadata about the pagination state and navigation
 *
 * @see UserResponseDto
 * @see PaginationDto
 * @see com.omaroid.presentation.mappers.UserMapper
 */
@Serializable
data class UsersPageResponseDto(
    val users: List<UserResponseDto>,
    val pagination: PaginationDto,
)
