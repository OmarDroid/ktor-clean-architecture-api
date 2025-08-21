package com.omaroid.di

import com.omaroid.presentation.controllers.UserController
import org.koin.dsl.module

val presentationModule = module {
    single { UserController(get(), get(), get(), get(), get()) }
}