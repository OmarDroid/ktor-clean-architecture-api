package com.omaroid.di

import com.omaroid.domain.usecases.CreateUserUseCase
import com.omaroid.domain.usecases.DeleteUserUseCase
import com.omaroid.domain.usecases.GetAllUsersUseCase
import com.omaroid.domain.usecases.GetUserUseCase
import com.omaroid.domain.usecases.UpdateUserUseCase
import org.koin.dsl.module

val domainModule = module {

    single { CreateUserUseCase(get()) }

    single { GetUserUseCase(get()) }

    single { UpdateUserUseCase(get()) }

    single { DeleteUserUseCase(get()) }

    single { GetAllUsersUseCase(get()) }
}