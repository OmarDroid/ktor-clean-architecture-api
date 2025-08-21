package presentation.routes

import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.repositories.UserRepository
import com.omaroid.domain.usecases.CreateUserUseCase
import com.omaroid.domain.usecases.DeleteUserUseCase
import com.omaroid.domain.usecases.GetAllUsersUseCase
import com.omaroid.domain.usecases.GetUserUseCase
import com.omaroid.domain.usecases.UpdateUserUseCase
import com.omaroid.presentation.controllers.UserController
import com.omaroid.presentation.dto.ApiResponse
import com.omaroid.presentation.dto.CreateUserRequestDto
import com.omaroid.presentation.dto.UpdateUserRequestDto
import com.omaroid.presentation.dto.UserResponseDto
import com.omaroid.presentation.dto.UsersPageResponseDto
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.datetime.Instant
import org.junit.Rule
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.koin.test.inject
import util.TestApplicationConfig.createConfiguredClient
import util.TestApplicationConfig.setupTestApplication
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserRoutesTest : KoinTest {

    // This rule will automatically start and stop Koin for each test
    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            module {
                single<UserRepository> { mockk() } // Start with a generic mock
                single { CreateUserUseCase(get()) }
                single { GetUserUseCase(get()) }
                single { UpdateUserUseCase(get()) }
                single { DeleteUserUseCase(get()) }
                single { GetAllUsersUseCase(get()) }
                single { UserController(get(), get(), get(), get(), get()) }
            }
        )
    }

    private val mockRepository: UserRepository by inject()
    private lateinit var testUser: User

    // Test data constants
    private val testEmailString = "test@example.com"
    private val testUserName = "John Doe"
    private val testUserId = 1L
    private val updatedName = "Updated Name"
    private val blankName = "   "
    private val invalidEmail = "invalid-email"
    private val invalidJson = "{invalid json}"
    private val nonexistentUserId = 999L

    // API endpoints
    private val usersEndpoint = "/api/v1/users"
    private val userByIdEndpoint = "/api/v1/users/1"
    private val nonexistentUserEndpoint = "/api/v1/users/$nonexistentUserId"
    private val invalidIdEndpoint = "/api/v1/users/invalid"
    private val paginatedEndpoint = "/api/v1/users?page=2&size=5"

    private val testEmail = Email(testEmailString)
    private val testUserIdValue = UserId(testUserId)
    private val nonexistentUserIdValue = UserId(nonexistentUserId)

    @BeforeTest
    fun setup() {
        val now = Instant.fromEpochSeconds(1672574400)
        testUser = User(
            id = testUserIdValue,
            email = testEmail,
            name = testUserName,
            createdAt = now,
            updatedAt = now
        )
    }

    @AfterTest
    fun teardown() {
        clearAllMocks()
    }

    // MARK: - POST /users Tests

    @Test
    fun `POST users returns 201 with user data when created successfully`() = testApplication {
        // Arrange
        val request = CreateUserRequestDto(testEmailString, testUserName)
        coEvery { mockRepository.findByEmail(testEmail) } returns null
        coEvery { mockRepository.create(testEmail, testUserName) } returns testUser

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().post(usersEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        // Assert
        assertEquals(HttpStatusCode.Created, response.status)

        val apiResponse = response.body<ApiResponse<UserResponseDto>>()
        assertTrue(apiResponse.success)

        val userResponse = apiResponse.data
        assertNotNull(userResponse)
        assertEquals(testUserName, userResponse.name)
        assertEquals(testEmailString, userResponse.email)
        assertEquals(testUserId, userResponse.id)
        assertNotNull(userResponse.createdAt)
        assertNotNull(userResponse.updatedAt)

        coVerify { mockRepository.findByEmail(testEmail) }
        coVerify { mockRepository.create(testEmail, testUserName) }
    }

    @Test
    fun `POST users returns 409 when user already exists`() = testApplication {
        // Arrange
        val request = CreateUserRequestDto(testEmailString, testUserName)
        coEvery { mockRepository.findByEmail(testEmail) } returns testUser

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().post(usersEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        // Assert
        assertEquals(HttpStatusCode.Conflict, response.status)
        assertTrue(response.bodyAsText().contains("already exists"))
        coVerify { mockRepository.findByEmail(testEmail) }
    }

    @Test
    fun `POST users returns 400 for invalid JSON`() = testApplication {
        // Arrange
        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().post(usersEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(invalidJson)
        }

        // Assert
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST users returns 400 for invalid email format`() = testApplication {
        // Arrange
        val request = CreateUserRequestDto(invalidEmail, testUserName)

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().post(usersEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        // Assert
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Email must contain @ symbol"))
    }

    @Test
    fun `POST users returns 400 for blank name`() = testApplication {
        // Arrange
        val request = CreateUserRequestDto(testEmailString, "")
        coEvery { mockRepository.findByEmail(testEmail) } returns null

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().post(usersEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        // Assert
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Name cannot be blank"))
    }

    // MARK: - GET /users/{id} Tests

    @Test
    fun `GET users by id returns 200 with user data when user exists`() = testApplication {
        // Arrange
        coEvery { mockRepository.findById(testUserIdValue) } returns testUser

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().get(userByIdEndpoint)

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)

        val apiResponse = response.body<ApiResponse<UserResponseDto>>()
        assertTrue(apiResponse.success)

        val userResponse = apiResponse.data
        assertNotNull(userResponse)
        assertEquals(testUserName, userResponse.name)
        assertEquals(testEmailString, userResponse.email)
        assertEquals(testUserId, userResponse.id)
        assertNotNull(userResponse.createdAt)
        assertNotNull(userResponse.updatedAt)

        coVerify { mockRepository.findById(testUserIdValue) }
    }

    @Test
    fun `GET users by id returns 404 when user not found`() = testApplication {
        // Arrange
        coEvery { mockRepository.findById(nonexistentUserIdValue) } returns null

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().get(nonexistentUserEndpoint)

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("not found"))
        coVerify { mockRepository.findById(nonexistentUserIdValue) }
    }

    @Test
    fun `GET users by id returns 400 for invalid ID format`() = testApplication {
        // Arrange
        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().get(invalidIdEndpoint)

        // Assert
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    // MARK: - GET /users Tests

    @Test
    fun `GET users returns 200 with paginated results`() = testApplication {
        // Arrange
        val now = Instant.fromEpochSeconds(1672574400)
        val expectedUsers = listOf(
            User(
                id = UserId(1L),
                email = Email("user1@example.com"),
                name = "User 1",
                createdAt = now,
                updatedAt = now
            ),
            User(
                id = UserId(2L),
                email = Email("user2@example.com"),
                name = "User 2",
                createdAt = now,
                updatedAt = now
            )
        )
        val totalCount = 25
        coEvery { mockRepository.findAll(offset = 0, limit = 10) } returns expectedUsers
        coEvery { mockRepository.getTotalCount() } returns totalCount

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().get(usersEndpoint)

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val apiResponse = response.body<ApiResponse<UsersPageResponseDto>>()
        assertTrue(apiResponse.success)

        val pageResponse = apiResponse.data
        assertNotNull(pageResponse)
        assertEquals(2, pageResponse.users.size)
        assertEquals(totalCount, pageResponse.pagination.total)
        assertEquals(0, pageResponse.pagination.page)
        assertEquals(10, pageResponse.pagination.size)
        assertEquals("User 1", pageResponse.users[0].name)
        assertEquals("User 2", pageResponse.users[1].name)

        coVerify { mockRepository.findAll(offset = 0, limit = 10) }
        coVerify { mockRepository.getTotalCount() }
    }

    @Test
    fun `GET users handles custom pagination parameters`() = testApplication {
        // Arrange
        val now = Instant.fromEpochSeconds(1672574400)
        val expectedUsers = listOf(
            User(
                id = UserId(3L),
                email = Email("user3@example.com"),
                name = "User 3",
                createdAt = now,
                updatedAt = now
            )
        )
        val totalCount = 15
        coEvery { mockRepository.findAll(offset = 10, limit = 5) } returns expectedUsers
        coEvery { mockRepository.getTotalCount() } returns totalCount

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().get(paginatedEndpoint)

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)
        val apiResponse = response.body<ApiResponse<UsersPageResponseDto>>()
        assertTrue(apiResponse.success)

        val pageResponse = apiResponse.data
        assertNotNull(pageResponse)
        assertEquals(1, pageResponse.users.size)
        assertEquals(totalCount, pageResponse.pagination.total)
        assertEquals(2, pageResponse.pagination.page)
        assertEquals(5, pageResponse.pagination.size)
        assertEquals("User 3", pageResponse.users[0].name)

        coVerify { mockRepository.findAll(offset = 10, limit = 5) }
        coVerify { mockRepository.getTotalCount() }
    }

    // MARK: - PUT /users/{id} Tests

    @Test
    fun `PUT users returns 200 with updated user when successful`() = testApplication {
        // Arrange
        val request = UpdateUserRequestDto(updatedName)
        val updatedUser = testUser.copy(name = updatedName)
        coEvery { mockRepository.findById(testUserIdValue) } returns testUser
        coEvery { mockRepository.update(any()) } returns updatedUser

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().put(userByIdEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)

        val apiResponse = response.body<ApiResponse<UserResponseDto>>()
        assertTrue(apiResponse.success)

        val userResponse = apiResponse.data
        assertNotNull(userResponse)
        assertEquals(updatedName, userResponse.name)
        assertNotNull(userResponse.createdAt)
        assertNotNull(userResponse.updatedAt)

        coVerify { mockRepository.findById(testUserIdValue) }
        coVerify { mockRepository.update(any()) }
    }

    @Test
    fun `PUT users returns 404 when user not found`() = testApplication {
        // Arrange
        val request = UpdateUserRequestDto(updatedName)
        coEvery { mockRepository.findById(nonexistentUserIdValue) } returns null

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().put(nonexistentUserEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("not found"))
        coVerify { mockRepository.findById(nonexistentUserIdValue) }
    }

    @Test
    fun `PUT users returns 400 for blank name`() = testApplication {
        // Arrange
        val request = UpdateUserRequestDto(blankName)
        coEvery { mockRepository.findById(testUserIdValue) } returns testUser

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().put(userByIdEndpoint) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        // Assert
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(response.bodyAsText().contains("Name cannot be blank"))
    }

    // MARK: - DELETE /users/{id} Tests

    @Test
    fun `DELETE users returns 204 when user deleted successfully`() = testApplication {
        // Arrange
        coEvery { mockRepository.findById(testUserIdValue) } returns testUser
        coEvery { mockRepository.deleteById(testUserIdValue) } returns true

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().delete(userByIdEndpoint)

        // Assert
        assertEquals(HttpStatusCode.NoContent, response.status)
        assertEquals("", response.bodyAsText())
        coVerify { mockRepository.findById(testUserIdValue) }
        coVerify { mockRepository.deleteById(testUserIdValue) }
    }

    @Test
    fun `DELETE users returns 404 when user not found`() = testApplication {
        // Arrange
        coEvery { mockRepository.findById(nonexistentUserIdValue) } returns null

        application {
            setupTestApplication()
        }

        // Act
        val response = createConfiguredClient().delete(nonexistentUserEndpoint)

        // Assert
        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("not found"))
        coVerify { mockRepository.findById(nonexistentUserIdValue) }
    }
}
