package com.omaroid.data.repository

import com.omaroid.data.database.tables.UsersTable
import com.omaroid.data.database.tables.UsersTable.createdAt
import com.omaroid.data.database.tables.UsersTable.email
import com.omaroid.data.database.tables.UsersTable.name
import com.omaroid.data.database.tables.UsersTable.updatedAt
import com.omaroid.domain.entities.Email
import com.omaroid.domain.entities.User
import com.omaroid.domain.entities.UserId
import com.omaroid.domain.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock.System.now
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class UserRepositoryImpl(private val database: Database) : UserRepository {

    override suspend fun create(email: Email, name: String): User = dbQuery {
        val now = now()

        val insertResult = UsersTable.insert {
            it[UsersTable.email] = email.value
            it[UsersTable.name] = name
            it[createdAt] = now
            it[updatedAt] = now
        }

        val id = insertResult[UsersTable.id]

        User(
            id = UserId(id.value), email = email, name = name, createdAt = now, updatedAt = now
        )
    }

    override suspend fun findById(id: UserId): User? = dbQuery {
        UsersTable.select { UsersTable.id eq id.value }.singleOrNull()?.toUser()
    }

    override suspend fun findByEmail(email: Email): User? = dbQuery {
        UsersTable.select { UsersTable.email eq email.value }.singleOrNull()?.toUser()
    }

    override suspend fun update(user: User): User = dbQuery {
        val now = now()

        UsersTable.update({ UsersTable.id eq user.id.value }) {
            it[email] = user.email.value
            it[name] = user.name
            it[updatedAt] = now
        }

        user.copy(updatedAt = now)
    }

    override suspend fun deleteById(id: UserId): Boolean = dbQuery {
        UsersTable.deleteWhere { UsersTable.id eq id.value } > 0
    }

    override suspend fun findAll(offset: Int, limit: Int): List<User> = dbQuery {
        UsersTable.selectAll().orderBy(UsersTable.id to SortOrder.ASC).limit(limit, offset.toLong())
            .map { it.toUser() }
    }

    override suspend fun getTotalCount(): Int = dbQuery {
        UsersTable.selectAll().count().toInt()
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }

    private fun ResultRow.toUser(): User = User(
        id = UserId(this[UsersTable.id].value),
        email = Email(this[email]),
        name = this[name],
        createdAt = this[createdAt],
        updatedAt = this[updatedAt]
    )
}