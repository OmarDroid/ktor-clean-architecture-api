package com.omaroid.di

import com.omaroid.config.DatabaseConfig
import com.omaroid.data.database.tables.UsersTable
import com.omaroid.data.repository.UserRepositoryImpl
import com.omaroid.domain.repositories.UserRepository
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module

val dataModule = module {

    single<Database> {
        val dbConfig = get<DatabaseConfig>() // Get the config from Koin

        try {
            Database.connect(
                url = dbConfig.url,
                driver = dbConfig.driver,
                user = dbConfig.user,
                password = dbConfig.password
            ).also { database ->
                // NOTE: In production, replace this with proper database migrations
                // This is only for development/demo purposes
                transaction(database) {
                    SchemaUtils.create(UsersTable)
                }
                println("Database connected successfully: ${dbConfig.url}")
            }
        } catch (e: Exception) {
            println("Failed to connect to database: ${e.message}")
            throw IllegalStateException("Database connection failed", e)
        }
    }

    single<UserRepository> { UserRepositoryImpl(get()) }
}