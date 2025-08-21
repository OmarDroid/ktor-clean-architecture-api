package com.omaroid.data.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

object UsersTable : LongIdTable("users") {
    /** User's email address - unique across the system */
    val email = varchar("email", 255).uniqueIndex("idx_users_email")

    /** User's display name */
    val name = varchar("name", 255)

    /** Timestamp when the user record was created */
    val createdAt = timestamp("created_at")

    /** Timestamp when the user record was last updated */
    val updatedAt = timestamp("updated_at")
}