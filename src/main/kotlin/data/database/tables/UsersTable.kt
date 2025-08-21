/**
 * Database table definitions using Exposed ORM.
 *
 * This package contains table schema definitions that map domain entities
 * to database structures using the Exposed ORM framework.
 */
package com.omaroid.data.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp

/**
 * Database table definition for user entities using Exposed ORM.
 *
 * Defines the schema for the "users" table including all columns,
 * data types, constraints, and indexes. This table stores core user
 * information with proper normalization and indexing for performance.
 *
 * Table Structure:
 * - id: Primary key (auto-incrementing long)
 * - email: Unique user email with index for fast lookups
 * - name: User display name
 * - created_at: Record creation timestamp
 * - updated_at: Last modification timestamp
 *
 * Indexes:
 * - Primary key on id (automatic)
 * - Unique index on email (idx_users_email)
 *
 * @see org.jetbrains.exposed.dao.id.LongIdTable
 * @see com.omaroid.domain.entities.User
 */
object UsersTable : LongIdTable("users") {

    /**
     * User's email address - unique across the system.
     *
     * VARCHAR(255) with unique constraint and index for fast email-based lookups.
     * This field enforces email uniqueness at the database level.
     */
    val email = varchar("email", 255).uniqueIndex("idx_users_email")

    /**
     * User's display name.
     *
     * VARCHAR(255) storing the user's full name or display name.
     * No constraints as business logic handles validation.
     */
    val name = varchar("name", 255)

    /**
     * Timestamp when the user record was created.
     *
     * Stores the exact moment when the user was first created in the system.
     * Set once during creation and never modified.
     */
    val createdAt = timestamp("created_at")

    /**
     * Timestamp when the user record was last updated.
     *
     * Automatically updated whenever any user data is modified.
     * Used for tracking data freshness and audit trails.
     */
    val updatedAt = timestamp("updated_at")
}