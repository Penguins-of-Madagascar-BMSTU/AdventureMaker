package com.softcat.adventuremaker.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
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
import com.softcat.adventuremaker.screens.createPost.CreatePostContent
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
fun MainScreen() {
    var selectedTab by rememberSaveable { mutableStateOf(NavigationItem.BottomBarConfiguration.Search) }

    // Отдельные контроллеры для каждой вкладки
    val navSearch = rememberNavController()
    val navFavourites = rememberNavController()
    val navNetworking = rememberNavController()
    val navTools = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                configuration = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(bottom = innerPadding.calculateBottomPadding())
                .fillMaxSize()
        ) {
            when (selectedTab) {
                NavigationItem.BottomBarConfiguration.Search -> SearchNavHost(navSearch)
                NavigationItem.BottomBarConfiguration.Favourites -> FavouritesNavHost(navFavourites)
                NavigationItem.BottomBarConfiguration.Networking -> NetworkingNavHost(navNetworking)
                NavigationItem.BottomBarConfiguration.Tools -> ToolsNavHost(navTools)
                else -> {}
            }
        }
    }
}

@Composable
private fun SearchNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Search.Map) {
        composable<NavigationItem.Search.Map> {
            MapSearchScreen(navController = navController)
        }
        composable<NavigationItem.Search.Details>(
            typeMap = mapOf(typeOf<Place>() to PlaceNavType)
        ) { entry ->
            val args = entry.toRoute<NavigationItem.Search.Details>()
            DetailsContent(args.place, navController)
        }
    }
}

@Composable
private fun FavouritesNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Favourites.Content) {
        composable<NavigationItem.Favourites.Content> {
            FavouritesContent(
                onPlaceClick = {
                    navController.navigate(NavigationItem.Favourites.PlaceDetails(it))
                }
            )
        }
        composable<NavigationItem.Favourites.PlaceDetails>(typeMap = mapOf(typeOf<Place>() to PlaceNavType)) { entry ->
            val args = entry.toRoute<NavigationItem.Favourites.PlaceDetails>()
            DetailsContent(args.place, navController)
        }
    }
}

@Composable
private fun NetworkingNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Networking.Posts) {
        composable<NavigationItem.Networking.Posts> { PostsFeedContent(navController) }
        composable<NavigationItem.Networking.Profile>(typeMap = mapOf(typeOf<User>() to UserNavType)) { entry ->
            val args = entry.toRoute<NavigationItem.Networking.Profile>()
            ProfileContent(navController, args.user)
        }
        composable<NavigationItem.Networking.CreatePost> { entry ->
            val args = entry.toRoute<NavigationItem.Networking.CreatePost>()
            CreatePostContent(navController, args.userId)
        }
        composable<NavigationItem.Networking.Auth> { AuthScreen(navController) }
    }
}

@Composable
private fun ToolsNavHost(navController: NavHostController) {
    NavHost(navController, startDestination = NavigationItem.Tools) {
        composable<NavigationItem.Tools> { ToolsContent(navController) }
    }
}