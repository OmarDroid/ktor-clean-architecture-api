package com.omaroid.di

import com.omaroid.domain.usecases.CreateUserUseCase
import com.omaroid.domain.usecases.DeleteUserUseCase
import com.omaroid.domain.usecases.GetAllUsersUseCase
import com.omaroid.domain.usecases.GetUserUseCase
import com.omaroid.domain.usecases.UpdateUserUseCase
import org.koin.dsl.module

/**
 * Dependency injection module for the domain layer.
 *
 * Configures all domain layer use cases that encapsulate business logic.
 * Each use case is configured as a singleton with its required repository
 * dependencies injected automatically by Koin.
 *
 * Configured Use Cases:
 * - CreateUserUseCase: Business logic for user creation
 * - GetUserUseCase: Business logic for retrieving individual users
 * - UpdateUserUseCase: Business logic for user updates
 * - DeleteUserUseCase: Business logic for user deletion
 * - GetAllUsersUseCase: Business logic for paginated user listings
 *
 * @see CreateUserUseCase
 * @see GetUserUseCase
 * @see UpdateUserUseCase
 * @see DeleteUserUseCase
 * @see GetAllUsersUseCase
 */
val domainModule = module {

    /**
     * Provides the user creation use case.
     *
     * Configures CreateUserUseCase with the UserRepository dependency
     * for handling user creation business logic.
     */
    single { CreateUserUseCase(get()) }

    /**
     * Provides the get user use case.
     *
     * Configures GetUserUseCase with the UserRepository dependency
     * for handling user retrieval business logic.
     */
    single { GetUserUseCase(get()) }

    /**
     * Provides the user update use case.
     *
     * Configures UpdateUserUseCase with the UserRepository dependency
     * for handling user modification business logic.
     */
    single { UpdateUserUseCase(get()) }

    /**
     * Provides the user deletion use case.
     *
     * Configures DeleteUserUseCase with the UserRepository dependency
     * for handling user removal business logic.
     */
    single { DeleteUserUseCase(get()) }

    /**
     * Provides the get all users use case.
     *
     * Configures GetAllUsersUseCase with the UserRepository dependency
     * for handling paginated user listing business logic.
     */
    single { GetAllUsersUseCase(get()) }
}