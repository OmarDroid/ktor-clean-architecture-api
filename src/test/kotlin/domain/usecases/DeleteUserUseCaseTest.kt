package domain.usecases

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository
import com.omaroid.domain.usecases.DeleteUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DeleteUserUseCaseTest {
    private lateinit var mockRepository: UserRepository
    private lateinit var deleteUserUseCase: DeleteUserUseCase

    @BeforeTest
    fun setup() {
        mockRepository = mockk<UserRepository>()
        deleteUserUseCase = DeleteUserUseCase(mockRepository)
    }

    @Test
    fun `should delete user successfully when user exists`() = runTest {
        // Given
        val userId = 1L
        val existingUser = User(
            id = UserId(userId),
            email = Email("user@example.com"),
            name = "User Name",
            createdAt = Instant.fromEpochSeconds(1672574400),
            updatedAt = Instant.fromEpochSeconds(1672574400)
        )

        coEvery { mockRepository.findById(UserId(userId)) } returns existingUser
        coEvery { mockRepository.deleteById(UserId(userId)) } returns true

        // When
        val result = deleteUserUseCase(userId)

        // Then
        assertEquals(true, result)
        coVerify { mockRepository.findById(UserId(userId)) }
        coVerify { mockRepository.deleteById(UserId(userId)) }
    }

    @Test
    fun `should throw NotFoundException when user does not exist`() = runTest {
        // Given
        val userId = 99L

        coEvery { mockRepository.findById(UserId(userId)) } returns null

        // When & Then
        val exception = assertFailsWith<AppException.NotFoundException> {
            deleteUserUseCase(userId)
        }
        assertEquals("User with ID $userId not found", exception.message)
        coVerify { mockRepository.findById(UserId(userId)) }
    }

    @Test
    fun `should return false when repository delete operation fails`() = runTest {
        // Given
        val userId = 1L
        val existingUser = User(
            id = UserId(userId),
            email = Email("user@example.com"),
            name = "User Name",
            createdAt = Instant.fromEpochSeconds(1672574400),
            updatedAt = Instant.fromEpochSeconds(1672574400)
        )

        coEvery { mockRepository.findById(UserId(userId)) } returns existingUser
        coEvery { mockRepository.deleteById(UserId(userId)) } returns false

        // When
        val result = deleteUserUseCase(userId)

        // Then
        assertEquals(false, result)
        coVerify { mockRepository.findById(UserId(userId)) }
        coVerify { mockRepository.deleteById(UserId(userId)) }
    }
}