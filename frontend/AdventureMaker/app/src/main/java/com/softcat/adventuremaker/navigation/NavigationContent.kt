package com.softcat.adventuremaker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.softcat.adventuremaker.screens.favourites.FavouritesContent
import com.softcat.adventuremaker.screens.networking.NetworkingContent
import com.softcat.adventuremaker.screens.search.SearchContent
import com.softcat.adventuremaker.screens.utils.UtilsContent

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
        startDestination = MainNavigation.Search
    ) {
        composable<MainNavigation.Favourites> {
            FavouritesContent(navController)
        }
        composable<MainNavigation.Search> {
            SearchContent()
        }
        composable<MainNavigation.Utils> {
            UtilsContent()
        }
        composable<MainNavigation.Networking> {
            NetworkingContent()
        }
    }
}