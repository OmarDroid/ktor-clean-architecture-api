package com.omaroid.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsersPageResponseDto(
    val users: List<UserResponseDto>,
    val pagination: PaginationDto,
)
