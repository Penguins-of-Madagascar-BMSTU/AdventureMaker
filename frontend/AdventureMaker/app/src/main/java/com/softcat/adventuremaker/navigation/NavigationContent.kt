package com.softcat.adventuremaker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.domain.entities.Place
import com.softcat.adventuremaker.navigation.NavTypes.PlaceNavType
import com.softcat.adventuremaker.screens.favourites.FavouritesContent
import kotlin.reflect.typeOf

/*
Это реализация навигации.
Каждая вершина в графе навигации задана функцией composable<T>.
T - тип, по которому navController определяет текущую конфигурацию экрана.
Когда создаётся новый экран, его следует прописать сюда.
 */
@Composable
fun NavigationContent() {
    val navController = rememberNavController()
    NavHost(
        modifier = Modifier,
        navController = navController,
        startDestination = NavigationItem.Favourites.Content
    ) {
        composable<NavigationItem.Favourites.Content> {
            FavouritesContent(
                navController = navController,
                onEnterClick = { navController.navigate(NavigationItem.Networking.Profile) },
                onPlaceClick = { navController.navigate(NavigationItem.Favourites.PlaceDetails(it)) }
            )
        }
        composable<NavigationItem.Search.Map> {}
        composable<NavigationItem.Search.Details>(
            typeMap = mapOf(typeOf<Place>() to PlaceNavType)
        ) {}
        composable<NavigationItem.Tools> {}
        composable<NavigationItem.Networking.Posts> {}
        composable<NavigationItem.Networking.Profile> {}
    }
}