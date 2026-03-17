package com.softcat.adventuremaker.di

import com.example.data.UserRepositoryImpl
import com.example.domain.interfaces.UserRepository
import com.example.domain.usecases.UserUseCase
import com.softcat.adventuremaker.screens.auth.AuthViewModel
import org.koin.dsl.module

val viewModelModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<UserUseCase> { UserUseCase(get()) }
    single<AuthViewModel> { AuthViewModel(get()) }
}