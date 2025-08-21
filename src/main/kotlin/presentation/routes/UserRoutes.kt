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

/**
 * Configures user management routes for the application.
 *
 * Sets up RESTful HTTP endpoints for user CRUD operations following standard
 * REST conventions. All routes are prefixed with `/api/v1/users` for API
 * versioning and organization.
 *
 * Configured Routes:
 * - GET `/` - Root endpoint (Hello World)
 * - GET `/api/v1/users` - List all users with pagination
 * - POST `/api/v1/users` - Create a new user
 * - GET `/api/v1/users/{id}` - Get user by ID
 * - PUT `/api/v1/users/{id}` - Update user by ID
 * - DELETE `/api/v1/users/{id}` - Delete user by ID
 *
 * @receiver Application The Ktor application instance to configure
 *
 * @see UserController
 */
fun Application.configureRoutes() {
    val userController by inject<UserController>()
    routing {

        /**
         * Root endpoint providing a simple health/welcome message.
         *
         * GET /
         * Response: Plain text "Hello World!"
         */
        get("/") {
            call.respondText("Hello World!")
        }

        /**
         * User management API routes grouped under `/api/v1/users`.
         *
         * Provides full CRUD operations for user entities with proper
         * HTTP method mapping and RESTful URL structure.
         */
        route("/api/v1/users") {

            /**
             * List users with optional pagination.
             *
             * GET /api/v1/users?page=0&size=10
             *
             * Query Parameters:
             * - page: Page number (0-based, default: 0)
             * - size: Page size (1-100, default: 10)
             *
             * Response: 200 OK with paginated user list
             */
            get {
                userController.getAllUsers(call)
            }

            /**
             * Create a new user.
             *
             * POST /api/v1/users
             * Content-Type: application/json
             * Body: CreateUserRequestDto
             *
             * Response: 201 Created with created user data
             */
            post {
                userController.createUser(call)
            }

            /**
             * Get a specific user by ID.
             *
             * GET /api/v1/users/{id}
             *
             * Path Parameters:
             * - id: User's unique identifier (must be positive integer)
             *
             * Response: 200 OK with user data, 404 if not found
             */
            get("/{id}") {
                userController.getUserById(call)
            }

            /**
             * Update an existing user.
             *
             * PUT /api/v1/users/{id}
             * Content-Type: application/json
             * Body: UpdateUserRequestDto
             *
             * Path Parameters:
             * - id: User's unique identifier (must be positive integer)
             *
             * Response: 200 OK with updated user data, 404 if not found
             */
            put("/{id}") {
                userController.updateUser(call)
            }

            /**
             * Delete a user by ID.
             *
             * DELETE /api/v1/users/{id}
             *
             * Path Parameters:
             * - id: User's unique identifier (must be positive integer)
             *
             * Response: 204 No Content if successful, 404 if not found
             */
            delete("/{id}") {
                userController.deleteUser(call)
            }
        }
    }
}