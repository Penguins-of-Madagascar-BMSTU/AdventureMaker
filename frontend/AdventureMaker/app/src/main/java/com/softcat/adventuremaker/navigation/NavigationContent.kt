package com.softcat.adventuremaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.domain.entities.Place
import com.example.domain.entities.User
import com.softcat.adventuremaker.navigation.NavTypes.PlaceNavType
import com.softcat.adventuremaker.navigation.NavTypes.UserNavType
import com.softcat.adventuremaker.screens.auth.AuthScreen
import com.softcat.adventuremaker.screens.details.DetailsContent
import com.softcat.adventuremaker.screens.favourites.FavouritesContent
import com.softcat.adventuremaker.screens.feed.PostsFeedContent
import com.softcat.adventuremaker.screens.profile.ProfileContent
import com.softcat.adventuremaker.screens.posts.CreatePostContent
import com.softcat.adventuremaker.screens.tools.ToolsContent
import com.softcat.adventuremaker.screens.search.MapSearchScreen
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
        startDestination = NavigationItem.Search.Map
    ) {
        composable<NavigationItem.Favourites.Content> {
            FavouritesContent(
                navController = navController,
                onProfileClick = {},
                onPlaceClick = { navController.navigate(NavigationItem.Favourites.PlaceDetails(it)) }
            )
        }
        composable<NavigationItem.Favourites.PlaceDetails>(
            typeMap = mapOf(typeOf<Place>() to PlaceNavType)
        ) { entry ->
            val args = entry.toRoute<NavigationItem.Favourites.PlaceDetails>()
            DetailsContent(args.place, navController)
        }
        composable<NavigationItem.Search.Map> {
            MapSearchScreen(navController = navController)
        }
        composable<NavigationItem.Search.Details>(
            typeMap = mapOf(typeOf<Place>() to PlaceNavType)
        ) {}
        composable<NavigationItem.Tools> {
            ToolsContent(navController = navController)
        }
        composable<NavigationItem.Networking.Posts> {
            PostsFeedContent(navController)
        }
        composable<NavigationItem.Networking.Profile>(
            typeMap = mapOf(typeOf<User>() to UserNavType)
        ) { entry ->
            val args = entry.toRoute<NavigationItem.Networking.Profile>()
            ProfileContent(navController, args.user)
        }
        composable<NavigationItem.Networking.CreatePost> {
            CreatePostContent(navController = navController)
        }
        composable<NavigationItem.Networking.Auth> {
            AuthScreen(navController = navController)
        }
    }
}
