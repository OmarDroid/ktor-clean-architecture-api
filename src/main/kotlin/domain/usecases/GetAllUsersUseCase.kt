package com.omaroid.domain.usecases

import com.omaroid.domain.entities.User
import com.omaroid.domain.repositories.UserRepository

class GetAllUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 10): Pair<List<User>, Int> {
        require(page >= 0) { "Page must be non-negative" }
        require(size > 0) { "Size must be positive" }
        require(size <= 100) { "Size cannot exceed 100" }

        val offset = page * size
        val users = userRepository.findAll(offset, size)
        val totalCount = userRepository.getTotalCount()
        return Pair(users, totalCount)
    }
}