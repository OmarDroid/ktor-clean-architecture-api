package com.omaroid.presentation.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
