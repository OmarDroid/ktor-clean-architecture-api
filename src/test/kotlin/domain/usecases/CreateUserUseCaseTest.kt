package domain.usecases

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.errors.AppException
import com.omaroid.domain.repositories.UserRepository
import com.omaroid.domain.usecases.CreateUserUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CreateUserUseCaseTest {
    private lateinit var mockRepository: UserRepository
    private lateinit var createUserUseCase: CreateUserUseCase

    @BeforeTest
    fun setUp() {
        mockRepository = mockk<UserRepository>()
        createUserUseCase = CreateUserUseCase(mockRepository)
    }

    @Test
    fun `should create user successfully when email is unique`() = runTest {
        // Given
        val email = "test@example.com"
        val name = "John Doe"
        val emailValue = Email(email)
        val now = Instant.fromEpochSeconds(1672574400) // 2023-01-01T12:00:00Z
        val expectedUser = User(
            id = UserId(1L), email = emailValue, name = name, createdAt = now, updatedAt = now
        )

        coEvery { mockRepository.findByEmail(emailValue) } returns null
        coEvery { mockRepository.create(emailValue, name) } returns expectedUser

        //When
        val user = createUserUseCase(email, name)

        //Then
        assertEquals(expectedUser, user)
        coVerify { mockRepository.findByEmail(emailValue) }
        coVerify { mockRepository.create(emailValue, name) }
    }

    @Test
    fun `should throw IllegalArgumentException when email is not unique`() = runTest {
        // Given
        val email = "test@example.com"
        val name = "John Doe"
        val emailValue = Email(email)
        val existingUser = User(
            id = UserId(1L),
            email = emailValue,
            name = "Existing User",
            createdAt = Instant.fromEpochSeconds(1672574400), // 2023-01-01T12:00:00Z
            updatedAt = Instant.fromEpochSeconds(1672574400)  // 2023-01-01T12:00:00Z
        )

        coEvery { mockRepository.findByEmail(emailValue) } returns existingUser

        // When & Then
        val exception = assertFailsWith<AppException.ConflictException> {
            createUserUseCase(email, name)
        }
        assertEquals("User with email $email already exists", exception.message)
        coVerify { mockRepository.findByEmail(emailValue) }
    }

    @Test
    fun `should throw BadRequestException when name is blank`() = runTest {
        // Given
        val email = "test@example.com"
        val blankName = "   "

        // When & Then
        val exception = assertFailsWith<AppException.BadRequestException> {
            createUserUseCase(email, blankName)
        }
        assertEquals("Name cannot be blank", exception.message)
    }

    @Test
    fun `should throw BadRequestException when email is invalid`() = runTest {
        // Given
        val invalidEmail = "invalid-email"
        val name = "John Doe"

        // When & Then
        val exception = assertFailsWith<AppException.BadRequestException> {
            createUserUseCase(invalidEmail, name)
        }
        assertEquals("Email must contain @ symbol", exception.message)
    }
}