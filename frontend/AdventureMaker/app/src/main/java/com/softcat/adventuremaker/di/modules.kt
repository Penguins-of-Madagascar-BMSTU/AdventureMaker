package com.softcat.adventuremaker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.data.api.CurrencyApiService
import com.example.data.CurrencyConverterRepositoryImpl
import com.example.data.EmergencyNumbersRepositoryImpl
import com.example.data.FavouriteRepositoryImpl
import com.example.data.PlaceImageProvider
import com.example.data.PlacesRepositoryImpl
import com.example.data.PostsRepositoryImpl
import com.example.data.TranslationRepositoryImpl
import com.example.data.UserRepositoryImpl
import com.example.data.api.CurrencyApiFactory
import com.example.data.api.MapsApiFactory
import com.example.data.api.MapsApiService
import com.example.domain.entities.Place
import com.example.domain.interfaces.CurrencyConverterRepository
import com.example.domain.interfaces.EmergencyNumbersRepository
import com.example.domain.interfaces.FavouriteRepository
import com.example.domain.interfaces.PlacesRepository
import com.example.domain.interfaces.PostsRepository
import com.example.domain.interfaces.TranslationRepository
import com.example.domain.interfaces.UserRepository
import com.example.domain.usecases.ConvertCurrencyUseCase
import com.example.domain.usecases.FavouriteUseCase
import com.example.domain.usecases.GetEmergencyNumbersUseCase
import com.example.domain.usecases.PlacesUseCase
import com.example.domain.usecases.PostsUseCase
import com.example.domain.usecases.TranslateTextUseCase
import com.example.domain.usecases.UserUseCase
import com.softcat.adventuremaker.AdventureMakerApplication
import com.softcat.adventuremaker.screens.auth.AuthViewModel
import com.softcat.adventuremaker.screens.details.PlaceDetailsViewModel
import com.softcat.adventuremaker.screens.favourites.FavouriteViewModel
import com.softcat.adventuremaker.screens.feed.PostsViewModel
import com.softcat.adventuremaker.screens.posts.CreatePostViewModel
import com.softcat.adventuremaker.screens.profile.ProfileViewModel
import com.softcat.adventuremaker.screens.search.SearchViewModel
import com.softcat.adventuremaker.screens.search.model.SearchViewModelMapper
import com.softcat.adventuremaker.screens.tools.ToolsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AuthViewModel)
    viewModelOf(::ToolsViewModel)
    viewModelOf(::FavouriteViewModel)
    viewModelOf(::CreatePostViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SearchViewModel)
    viewModel { PostsViewModel(get()) }

    viewModel { (place: Place) ->
        PlaceDetailsViewModel(place, get(), get())
    }
}

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<TranslationRepository> { TranslationRepositoryImpl() }
    single<EmergencyNumbersRepository> { EmergencyNumbersRepositoryImpl() }
    single<CurrencyConverterRepository> { CurrencyConverterRepositoryImpl(get()) }
    single<FavouriteRepository> { FavouriteRepositoryImpl(get()) }
    single<PlacesRepository> { PlacesRepositoryImpl(get(), get()) }
    single<PostsRepository> { PostsRepositoryImpl() }

    single { UserUseCase(get()) }
    single { FavouriteUseCase(get()) }
    single { PostsUseCase(get()) }
    single { TranslateTextUseCase(get()) }
    single { GetEmergencyNumbersUseCase(get()) }
    single { ConvertCurrencyUseCase(get()) }
    single { PlacesUseCase(get()) }
}


val dataModule = module {

    single<CurrencyApiService> {
        CurrencyApiFactory.currencyApi
    }
    single<MapsApiService> { MapsApiFactory.apiService }

    single<DataStore<Preferences>> {
        (get<Context>() as AdventureMakerApplication).dataStore
    }

    single { PlaceImageProvider() }
    single { SearchViewModelMapper() }
}