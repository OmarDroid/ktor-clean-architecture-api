package com.omaroid

import com.omaroid.config.AppConfig
import com.omaroid.config.loadConfig
import com.omaroid.data.database.tables.UsersTable
import com.omaroid.data.repository.UserRepositoryImpl
import com.omaroid.domain.repositories.UserRepository
import com.omaroid.domain.usecases.CreateUserUseCase
import com.omaroid.domain.usecases.DeleteUserUseCase
import com.omaroid.domain.usecases.GetAllUsersUseCase
import com.omaroid.domain.usecases.GetUserUseCase
import com.omaroid.domain.usecases.UpdateUserUseCase
import com.omaroid.presentation.controllers.UserController
import com.omaroid.presentation.plugins.configureStatusPages
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

    val database = Database.connect(
        url = appConfig.database.url,
        driver = appConfig.database.driver,
        user = appConfig.database.user,
        password = appConfig.database.password
    )

    transaction(database) {
        SchemaUtils.create(UsersTable)
    }

    val userRepository: UserRepository = UserRepositoryImpl(database)
    val createUserUseCase = CreateUserUseCase(userRepository)
    val getUserUseCase = GetUserUseCase(userRepository)
    val updateUserUseCase = UpdateUserUseCase(userRepository)
    val deleteUserUseCase = DeleteUserUseCase(userRepository)
    val getAllUsersUseCase = GetAllUsersUseCase(userRepository)

    val userController = UserController(
        createUserUseCase, getUserUseCase, updateUserUseCase, deleteUserUseCase, getAllUsersUseCase
    )

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    configureStatusPages()
    configureRoutes(userController)
}
