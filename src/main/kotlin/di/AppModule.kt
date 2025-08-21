/**
 * Dependency injection modules for the application.
 *
 * This package contains Koin dependency injection module definitions
 * that configure object creation and dependency wiring throughout
 * the application layers following clean architecture principles.
 */
package com.omaroid.di

import com.omaroid.config.AppConfig
import org.koin.dsl.module

/**
 * Creates the root application module for dependency injection.
 *
 * Aggregates all layer-specific modules and provides the application
 * configuration objects to the dependency injection container. This
 * is the main entry point for dependency configuration.
 *
 * @param appConfig The validated application configuration containing all settings
 * @return Koin module configured with all application dependencies
 *
 * @see AppConfig
 * @see dataModule
 * @see domainModule
 * @see presentationModule
 */
fun createAppModule(appConfig: AppConfig) = module {
    // Provide the fully resolved, type-safe config objects to the DI container
    single<AppConfig> { appConfig }
    single { appConfig.database }
    single { appConfig.server }

    // Include all layer modules
    includes(dataModule, domainModule, presentationModule)
}