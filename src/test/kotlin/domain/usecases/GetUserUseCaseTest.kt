package domain.usecases

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository
import com.omaroid.domain.usecases.GetUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetUserUseCaseTest {
    private lateinit var mockRepository: UserRepository
    private lateinit var getUserUseCase: GetUserUseCase

    @BeforeTest
    fun setup() {
        mockRepository = mockk<UserRepository>()
        getUserUseCase = GetUserUseCase(mockRepository)
    }

    @Test
    fun `should return user when user exists`() = runTest {
        // Given
        val userId = 1L
        val expectedUser = User(
            id = UserId(userId),
            email = Email("test@example.com"),
            name = "John Doe",
            createdAt = Instant.fromEpochSeconds(1672574400),
            updatedAt = Instant.fromEpochSeconds(1672574400)
        )

        coEvery { mockRepository.findById(UserId(userId)) } returns expectedUser

        // When
        val user = getUserUseCase(userId)

        // Then
        assertEquals(expectedUser, user)
        coVerify { mockRepository.findById(UserId(userId)) }
    }

    @Test
    fun `should throw NotFoundException when user does not exist`() = runTest {
        // Given
        val userId = 1L

        coEvery { mockRepository.findById(UserId(userId)) } returns null

        // When & Then
        val exception = assertFailsWith<AppException.NotFoundException> {
            getUserUseCase(userId)
        }
        assertEquals("User with ID $userId not found", exception.message)
        coVerify { mockRepository.findById(UserId(userId)) }
    }
}