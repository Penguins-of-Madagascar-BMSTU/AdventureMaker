package com.softcat.adventuremaker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.data.CurrencyApi
import com.example.data.CurrencyConverterRepositoryImpl
import com.example.data.EmergencyNumbersRepositoryImpl
import com.example.data.FavouriteRepositoryImpl
import com.example.data.TranslationRepositoryImpl
import com.example.data.UserRepositoryImpl
import com.example.domain.interfaces.CurrencyConverterRepository
import com.example.domain.interfaces.EmergencyNumbersRepository
import com.example.domain.interfaces.FavouriteRepository
import com.example.domain.interfaces.TranslationRepository
import com.example.domain.interfaces.UserRepository
import com.example.domain.usecases.ConvertCurrencyUseCase
import com.example.domain.usecases.FavouriteUseCase
import com.example.domain.usecases.GetEmergencyNumbersUseCase
import com.example.domain.usecases.TranslateTextUseCase
import com.example.domain.usecases.UserUseCase
import com.softcat.adventuremaker.AdventureMakerApplication
import com.softcat.adventuremaker.screens.auth.AuthViewModel
import com.softcat.adventuremaker.screens.favourites.FavouriteViewModel
import com.softcat.adventuremaker.screens.tools.ToolsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::ToolsViewModel)
    viewModelOf(::FavouriteViewModel)
}

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<TranslationRepository> { TranslationRepositoryImpl() }
    single<EmergencyNumbersRepository> { EmergencyNumbersRepositoryImpl() }
    single<CurrencyConverterRepository> { CurrencyConverterRepositoryImpl(get()) }
    single<FavouriteRepository> { FavouriteRepositoryImpl() }

    single { UserUseCase(get()) }
    single { FavouriteUseCase(get()) }
    single { TranslateTextUseCase(get()) }
    single { GetEmergencyNumbersUseCase(get()) }
    single { ConvertCurrencyUseCase(get()) }
}


val dataModule = module {

    single {
        Retrofit.Builder()
            .baseUrl("https://open.er-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<CurrencyApi> {
        get<Retrofit>().create(CurrencyApi::class.java)
    }

    single<DataStore<Preferences>> {
        (get<Context>() as AdventureMakerApplication).dataStore
    }
}