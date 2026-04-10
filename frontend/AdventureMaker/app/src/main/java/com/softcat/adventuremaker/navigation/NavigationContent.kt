package com.softcat.adventuremaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.domain.entities.Place
import com.softcat.adventuremaker.navigation.NavTypes.PlaceNavType
import com.softcat.adventuremaker.screens.details.DetailsContent
import com.softcat.adventuremaker.screens.favourites.FavouritesContent
import com.softcat.adventuremaker.screens.profile.ProfileContent
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
        composable<NavigationItem.Favourites.PlaceDetails>(
            typeMap = mapOf(typeOf<Place>() to PlaceNavType)
        ) { entry ->
            val args = entry.toRoute<NavigationItem.Favourites.PlaceDetails>()
            DetailsContent(args.place, navController)
        }
        composable<NavigationItem.Search.Map> {}
        composable<NavigationItem.Search.Details>(
            typeMap = mapOf(typeOf<Place>() to PlaceNavType)
        ) {}
        composable<NavigationItem.Tools> {}
        composable<NavigationItem.Networking.Posts> {}
        composable<NavigationItem.Networking.Profile> {
            ProfileContent(navController)
        }
    }
}