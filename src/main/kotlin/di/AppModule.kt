package com.omaroid.di

import com.omaroid.config.AppConfig
import org.koin.dsl.module

fun createAppModule(appConfig: AppConfig) = module {
    // Provide the fully resolved, type-safe config objects to the DI container
    single<AppConfig> { appConfig }
    single { appConfig.database }
    single { appConfig.server }

    // Include all layer modules
    includes(dataModule, domainModule, presentationModule)
}