package com.omaroid.di

import com.omaroid.presentation.controllers.UserController
import org.koin.dsl.module

/**
 * Dependency injection module for the presentation layer.
 *
 * Configures all presentation layer components including controllers
 * that handle HTTP requests and coordinate with use cases. Controllers
 * are configured with all their required use case dependencies.
 *
 * Configured Components:
 * - UserController: HTTP request handler for user management operations
 *
 * @see UserController
 */
val presentationModule = module {

    /**
     * Provides the user controller with all required use case dependencies.
     *
     * Configures UserController with all five user management use cases:
     * - CreateUserUseCase for user creation
     * - GetUserUseCase for individual user retrieval
     * - UpdateUserUseCase for user modifications
     * - DeleteUserUseCase for user deletion
     * - GetAllUsersUseCase for paginated user listings
     *
     * All dependencies are automatically injected by Koin.
     */
    single { UserController(get(), get(), get(), get(), get()) }
}