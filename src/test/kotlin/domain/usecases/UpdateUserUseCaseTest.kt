package domain.usecases

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository
import com.omaroid.domain.usecases.UpdateUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UpdateUserUseCaseTest {
    private lateinit var mockRepository: UserRepository
    private lateinit var updateUserUseCase: UpdateUserUseCase

    @BeforeTest
    fun setup() {
        mockRepository = mockk<UserRepository>()
        updateUserUseCase = UpdateUserUseCase(mockRepository)
    }

    @Test
    fun `should update user successfully when user exists and name is valid`() = runTest {
        // Given
        val userId = 1L
        val originalUser = User(
            id = UserId(userId),
            email = Email("user@example.com"),
            name = "Old Name",
            createdAt = Instant.fromEpochSeconds(1672574400),
            updatedAt = Instant.fromEpochSeconds(1672574400)
        )
        val newName = "New Name"
        val updatedUser = originalUser.copy(
            name = newName, updatedAt = Instant.fromEpochSeconds(1672660800)
        )

        coEvery { mockRepository.findById(UserId(userId)) } returns originalUser
        coEvery { mockRepository.update(any()) } returns updatedUser

        // When
        val result = updateUserUseCase(userId, newName)

        // Then
        assertEquals(newName, result.name)
        coVerify { mockRepository.findById(UserId(userId)) }
        coVerify { mockRepository.update(any()) }
    }

    @Test
    fun `should throw NotFoundException when user does not exist`() = runTest {
        // Given
        val userId = 99L
        val newName = "New Name"

        coEvery { mockRepository.findById(UserId(userId)) } returns null

        // When & Then
        val exception = assertFailsWith<AppException.NotFoundException> {
            updateUserUseCase(userId, newName)
        }
        assertEquals("User with ID $userId not found", exception.message)
        coVerify { mockRepository.findById(UserId(userId)) }
    }

    @Test
    fun `should throw BadRequestException when name is blank`() = runTest {
        // Given
        val userId = 1L
        val blankName = "   "

        // When & Then
        val exception = assertFailsWith<AppException.BadRequestException> {
            updateUserUseCase(userId, blankName)
        }
        assertEquals("Name cannot be blank", exception.message)
    }
}