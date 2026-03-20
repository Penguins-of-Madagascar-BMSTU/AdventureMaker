package com.softcat.adventuremaker.di

import com.example.data.CurrencyConverterRepositoryImpl
import com.example.data.EmergencyNumbersRepositoryImpl
import com.example.data.TranslationRepositoryImpl
import com.example.data.UserRepositoryImpl
import com.example.domain.interfaces.CurrencyConverterRepository
import com.example.domain.interfaces.EmergencyNumbersRepository
import com.example.domain.interfaces.TranslationRepository
import com.example.domain.interfaces.UserRepository
import com.example.domain.usecases.ConvertCurrencyUseCase
import com.example.domain.usecases.GetEmergencyNumbersUseCase
import com.example.domain.usecases.TranslateTextUseCase
import com.example.domain.usecases.UserUseCase
import com.softcat.adventuremaker.screens.auth.AuthViewModel
import com.softcat.adventuremaker.screens.tools.ToolsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<TranslationRepository> { TranslationRepositoryImpl() }
    single<EmergencyNumbersRepository> { EmergencyNumbersRepositoryImpl() }
    single<CurrencyConverterRepository> { CurrencyConverterRepositoryImpl() }

    single<UserUseCase> { UserUseCase(get()) }
    single<TranslateTextUseCase> { TranslateTextUseCase(get()) }
    single<GetEmergencyNumbersUseCase> { GetEmergencyNumbersUseCase(get()) }
    single<ConvertCurrencyUseCase> { ConvertCurrencyUseCase(get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { ToolsViewModel(get(), get(), get()) }
}
