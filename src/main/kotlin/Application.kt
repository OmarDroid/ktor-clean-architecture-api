package com.omaroid

import com.omaroid.config.loadConfig
import io.github.cdimascio.dotenv.Dotenv
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

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
        module = { module() }
    ).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    // Configure routing
    configureRouting()
}
