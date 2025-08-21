package data.repository

import com.omaroid.data.database.tables.UsersTable
import com.omaroid.data.repository.UserRepositoryImpl
import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.UserId
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.test.*

class UserRepositoryImplTest {
    private lateinit var database: Database
    private lateinit var repository: UserRepositoryImpl

    @BeforeTest
    fun setUp() {
        // Create H2 in-memory database with PostgreSQL compatibility mode
        database = Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL", driver = "org.h2.Driver"
        )

        // Initialize database schema and clean state
        transaction {
            SchemaUtils.create(UsersTable)
            UsersTable.deleteAll()
        }

        repository = UserRepositoryImpl(database)
    }

    @AfterTest
    fun tearDown() {
        // Clean up after each test to ensure isolation
        transaction { UsersTable.deleteAll() }
    }

    @Test
    fun `create should persist and return user with generated ID and timestamps`() = runTest {
        // Given
        val email = Email("john.doe@example.com")
        val name = "John Doe"

        // When
        val user = repository.create(email, name)

        // Then
        assertNotNull(user.id, "User ID should be generated")
        assertEquals(email, user.email, "Email should match input")
        assertEquals(name, user.name, "Name should match input")
        assertNotNull(user.createdAt, "Created timestamp should be set")
        assertNotNull(user.updatedAt, "Updated timestamp should be set")
    }

    @Test
    fun `findById should return previously created user`() = runTest {
        // Given
        val email = Email("jane.doe@example.com")
        val created = repository.create(email, "Jane Doe")

        // When
        val found = repository.findById(created.id)

        // Then
        assertNotNull(found, "User should be found")
        assertEquals(created.id, found.id, "IDs should match")
        assertEquals(created.email, found.email, "Emails should match")
        assertEquals(created.name, found.name, "Names should match")
    }

    @Test
    fun `findById should return null for non-existent user`() = runTest {
        // When
        val result = repository.findById(UserId(999))

        // Then
        assertNull(result, "Non-existent user should return null")
    }

    @Test
    fun `findByEmail should return user when email exists`() = runTest {
        // Given
        val email = Email("search@example.com")
        val created = repository.create(email, "Search User")

        // When
        val found = repository.findByEmail(email)

        // Then
        assertNotNull(found, "User should be found by email")
        assertEquals(created.id, found.id, "Found user should match created user")
    }

    @Test
    fun `update should modify user fields and update timestamp`() = runTest {
        // Given
        val created = repository.create(Email("update@test.com"), "Old Name")
        val originalUpdatedAt = created.updatedAt
        val updated = created.copy(name = "New Name")

        // When
        val result = repository.update(updated)

        // Then
        assertEquals("New Name", result.name, "Name should be updated")
        assertTrue(
            result.updatedAt > originalUpdatedAt, "Updated timestamp should be newer than original"
        )
    }

    @Test
    fun `deleteById should remove user and return true when user exists`() = runTest {
        // Given
        val created = repository.create(Email("remove@test.com"), "Remove Me")

        // When
        val deleted = repository.deleteById(created.id)

        // Then
        assertTrue(deleted, "Delete should return true for existing user")
        assertNull(
            repository.findById(created.id), "User should no longer exist after deletion"
        )
    }

    @Test
    fun `deleteById should return false for non-existent user`() = runTest {
        // When
        val deleted = repository.deleteById(UserId(999))

        // Then
        assertFalse(deleted, "Delete should return false for non-existent user")
    }

    @Test
    fun `findAll should return paginated results in correct order`() = runTest {
        // Given - Create test data
        (1..5).map { i ->
            repository.create(Email("user$i@example.com"), "User $i")
        }

        // When
        val firstPage = repository.findAll(offset = 0, limit = 3)
        val secondPage = repository.findAll(offset = 3, limit = 3)

        // Then
        assertEquals(3, firstPage.size, "First page should contain 3 users")
        assertEquals(2, secondPage.size, "Second page should contain 2 users")

        // Verify ordering by ID
        assertTrue(
            firstPage.zipWithNext().all { (a, b) -> a.id.value < b.id.value },
            "First page should be ordered by ID"
        )
    }
}