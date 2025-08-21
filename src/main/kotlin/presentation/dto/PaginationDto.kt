package com.omaroid.presentation.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object for pagination metadata in API responses.
 *
 * Provides comprehensive pagination information to help clients
 * navigate through paginated results. Includes current page information,
 * total counts, and navigation indicators.
 *
 * @property page Current page number (0-based indexing)
 * @property size Number of items per page (maximum items requested)
 * @property total Total number of items across all pages
 * @property totalPages Total number of pages available
 * @property hasNext Indicates if there are more pages after the current one
 * @property hasPrevious Indicates if there are pages before the current one
 *
 * @see UsersPageResponseDto
 */
@Serializable
data class PaginationDto(
    val page: Int,
    val size: Int,
    val total: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("has_next")
    val hasNext: Boolean,
    @SerialName("has_previous")
    val hasPrevious: Boolean,
)
