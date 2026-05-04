package com.softcat.adventuremaker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.data.api.CurrencyApiService
import com.example.data.CurrencyConverterRepositoryImpl
import com.example.data.EmergencyNumbersRepositoryImpl
import com.example.data.ExperimentsRepositoryImpl
import com.example.data.FavouriteRepositoryImpl
import com.example.data.LocationRepositoryImpl
import com.example.data.PlaceImageProvider
import com.example.data.PlacesRepositoryImpl
import com.example.data.PostsRepositoryImpl
import com.example.data.S3ImageLoader
import com.example.data.TranslationRepositoryImpl
import com.example.data.UserPostsRepositoryImpl
import com.example.data.UserRepositoryImpl
import com.example.data.api.CurrencyApiFactory
import com.example.data.api.MapsApiFactory
import com.example.data.api.MapsApiService
import com.example.domain.entities.Place
import com.example.domain.interfaces.CurrencyConverterRepository
import com.example.domain.interfaces.EmergencyNumbersRepository
import com.example.domain.interfaces.ExperimentsRepository
import com.example.domain.interfaces.FavouriteRepository
import com.example.domain.interfaces.LocationRepository
import com.example.domain.interfaces.PlacesRepository
import com.example.domain.interfaces.PostsRepository
import com.example.domain.interfaces.TranslationRepository
import com.example.domain.interfaces.UserPostsRepository
import com.example.domain.interfaces.UserRepository
import com.example.domain.usecases.ConvertCurrencyUseCase
import com.example.domain.usecases.ExperimentsUseCase
import com.example.domain.usecases.FavouriteUseCase
import com.example.domain.usecases.GetEmergencyNumbersUseCase
import com.example.domain.usecases.LocationUseCase
import com.example.domain.usecases.PlacesUseCase
import com.example.domain.usecases.PostsUseCase
import com.example.domain.usecases.TranslateTextUseCase
import com.example.domain.usecases.UserPostsUseCase
import com.example.domain.usecases.UserUseCase
import com.google.android.gms.location.LocationServices
import com.softcat.adventuremaker.AdventureMakerApplication
import com.softcat.adventuremaker.designElements.mappers.PostModelMapper
import com.softcat.adventuremaker.screens.auth.AuthViewModel
import com.softcat.adventuremaker.screens.details.PlaceDetailsViewModel
import com.softcat.adventuremaker.screens.favourites.FavouriteViewModel
import com.softcat.adventuremaker.screens.feed.PostsViewModel
import com.softcat.adventuremaker.screens.createPost.CreatePostViewModel
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
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SearchViewModel)

    viewModel { (place: Place) ->
        PlaceDetailsViewModel(place, get(), get())
    }
    viewModel { (userId: String) ->
        CreatePostViewModel(get(), userId, get(), get())
    }
    viewModel {
        PostsViewModel(get(), get(), get(), get())
    }
}

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<TranslationRepository> { TranslationRepositoryImpl() }
    single<EmergencyNumbersRepository> { EmergencyNumbersRepositoryImpl() }
    single<CurrencyConverterRepository> { CurrencyConverterRepositoryImpl(get()) }
    single<FavouriteRepository> { FavouriteRepositoryImpl(get()) }
    single<PlacesRepository> { PlacesRepositoryImpl(get(), get()) }
    single<PostsRepository> { PostsRepositoryImpl(get()) }
    single<LocationRepository> { LocationRepositoryImpl(get(), get<Context>()) }
    single<ExperimentsRepository> { ExperimentsRepositoryImpl(get()) }
    single<UserPostsRepository> { UserPostsRepositoryImpl(get()) }

    single { UserUseCase(get()) }
    single { FavouriteUseCase(get()) }
    single { PostsUseCase(get()) }
    single { TranslateTextUseCase(get()) }
    single { GetEmergencyNumbersUseCase(get()) }
    single { ConvertCurrencyUseCase(get()) }
    single { PlacesUseCase(get()) }
    single { LocationUseCase(get()) }
    single { ExperimentsUseCase(get()) }
    single { UserPostsUseCase(get()) }
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

    single {
        LocationServices.getFusedLocationProviderClient(get<Context>())
    }
    single { S3ImageLoader(get<Context>()) }
    single { PostModelMapper(get<Context>(), get()) }
}