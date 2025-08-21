package com.omaroid.presentation.routes

import com.omaroid.presentation.controllers.UserController
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRoutes() {
    val userController by inject<UserController>()
    routing {

        get("/") {
            call.respondText("Hello World!")
        }

        route("/api/v1/users") {

            // GET /api/v1/users - Get all users with pagination
            get {
                userController.getAllUsers(call)
            }

            // POST /api/v1/users - Create a new user
            post {
                userController.createUser(call)
            }

            // GET /api/v1/users/{id} - Get user by ID
            get("/{id}") {
                userController.getUserById(call)
            }

            // PUT /api/v1/users/{id} - Update user
            put("/{id}") {
                userController.updateUser(call)
            }

            // DELETE /api/v1/users/{id} - Delete user
            delete("/{id}") {
                userController.deleteUser(call)
            }
        }
    }
}