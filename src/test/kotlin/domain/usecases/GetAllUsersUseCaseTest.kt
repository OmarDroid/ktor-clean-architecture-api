package domain.usecases

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.repositories.UserRepository
import com.omaroid.domain.usecases.GetAllUsersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.*

class GetAllUsersUseCaseTest {
    private lateinit var mockRepository: UserRepository
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase

    @BeforeTest
    fun setup() {
        mockRepository = mockk<UserRepository>()
        getAllUsersUseCase = GetAllUsersUseCase(mockRepository)
    }

    @Test
    fun `should return users with total count and valid pagination`() = runTest {
        // Given
        val page = 0
        val size = 10
        val expectedUsers = listOf(
            User(
                id = UserId(1L),
                email = Email("user1@example.com"),
                name = "User 1",
                createdAt = Instant.fromEpochSeconds(1672574400), // 2023-01-01T12:00:00Z
                updatedAt = Instant.fromEpochSeconds(1672574400)  // 2023-01-01T12:00:00Z
            ), User(
                id = UserId(2L),
                email = Email("user2@example.com"),
                name = "User 2",
                createdAt = Instant.fromEpochSeconds(1672574400), // 2023-01-01T12:00:00Z
                updatedAt = Instant.fromEpochSeconds(1672574400)  // 2023-01-01T12:00:00Z
            )
        )
        val expectedTotalCount = 25

        coEvery { mockRepository.findAll(0, 10) } returns expectedUsers
        coEvery { mockRepository.getTotalCount() } returns expectedTotalCount

        // When
        val (users, totalCount) = getAllUsersUseCase(page, size)

        // Then
        assertEquals(expectedUsers, users)
        assertEquals(expectedTotalCount, totalCount)
        coVerify { mockRepository.findAll(0, 10) }
        coVerify { mockRepository.getTotalCount() }
    }

    @Test
    fun `should return empty list with zero total count when no users exist`() = runTest {
        // Given
        val page = 0
        val size = 10
        val emptyUsersList = emptyList<User>()
        val zeroTotalCount = 0

        coEvery { mockRepository.findAll(0, 10) } returns emptyUsersList
        coEvery { mockRepository.getTotalCount() } returns zeroTotalCount

        // When
        val (users, totalCount) = getAllUsersUseCase(page, size)

        // Then
        assertEquals(emptyUsersList, users)
        assertEquals(zeroTotalCount, totalCount)
        coVerify { mockRepository.findAll(0, 10) }
        coVerify { mockRepository.getTotalCount() }
    }

    @Test
    fun `should throw IllegalArgumentException with negative page number`() = runTest {
        // Given
        val page = -1
        val size = 10

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            getAllUsersUseCase(page, size)
        }
        assertEquals("Page must be non-negative", exception.message)
    }

    @Test
    fun `should throw IllegalArgumentException with invalid size`() = runTest {
        // Given
        val page = 0
        val invalidSize = 0

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            getAllUsersUseCase(page, invalidSize)
        }
        assertEquals("Size must be positive", exception.message)
    }

    @Test
    fun `should throw IllegalArgumentException when size exceeds limit`() = runTest {
        // Given
        val page = 0
        val tooLargeSize = 101

        // When & Then
        val exception = assertFailsWith<IllegalArgumentException> {
            getAllUsersUseCase(page, tooLargeSize)
        }
        assertEquals("Size cannot exceed 100", exception.message)
    }
}