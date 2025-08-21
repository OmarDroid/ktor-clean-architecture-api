package com.omaroid

import com.omaroid.config.AppConfig
import com.omaroid.config.loadConfig
import com.omaroid.data.database.tables.UsersTable
import com.omaroid.data.repository.UserRepositoryImpl
import com.omaroid.di.createAppModule
import com.omaroid.domain.repositories.UserRepository
import com.omaroid.domain.usecases.CreateUserUseCase
import com.omaroid.domain.usecases.DeleteUserUseCase
import com.omaroid.domain.usecases.GetAllUsersUseCase
import com.omaroid.domain.usecases.GetUserUseCase
import com.omaroid.domain.usecases.UpdateUserUseCase
import com.omaroid.presentation.controllers.UserController
import com.omaroid.presentation.plugins.configureStatusPages
import com.omaroid.presentation.routes.configureHealthRoutes
import com.omaroid.presentation.routes.configureRoutes
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    // For local development, load .env into System Properties
    Dotenv.configure().filename("database.env")
        .ignoreIfMissing()
        .systemProperties()
        .load()
    // Load the unified, type-safe configuration
    val appConfig = loadConfig()
    // Start the server with the loaded configuration
    embeddedServer(
        factory = CIO,
        port = appConfig.server.port,
        host = appConfig.server.host,
        module = { module(appConfig) }
    ).start(wait = true)
}

fun Application.module(appConfig: AppConfig) {
    // Configure Koin dependency injection
    install(Koin) {
        slf4jLogger()
        // Pass the config to Koin so it's available for injection throughout the app
        modules(createAppModule(appConfig))
        // This line is useful for allowing injection of the Application instance itself
        koin.declare(this@module)
    }

    // Configure content negotiation for JSON serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Configure application plugins and routing
    configureStatusPages()
    configureRoutes()
    configureHealthRoutes()
}