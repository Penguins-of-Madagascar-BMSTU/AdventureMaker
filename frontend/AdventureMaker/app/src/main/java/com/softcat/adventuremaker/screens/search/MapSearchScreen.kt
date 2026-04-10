package com.softcat.adventuremaker.screens.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.navigation.NavigationItem.BottomBarConfiguration
import com.softcat.adventuremaker.screens.search.components.CategoryChipsSection
import com.softcat.adventuremaker.screens.search.components.PlacesList
import com.softcat.adventuremaker.screens.search.model.SearchCategoryChipModel
import com.softcat.adventuremaker.screens.search.model.SearchPlaceItemModel
import com.softcat.adventuremaker.screens.search.model.SearchSheetMockData
import com.softcat.adventuremaker.ui.components.SheetDragHandle
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import com.softcat.adventuremaker.ui.theme.BasicOrange

private val SheetContainerColor = Color(0xFFF5F1EB)
private val MapBaseColor = Color(0xFFD8E2D4)
private val MapRoadColor = Color(0xFFFDFBF5)
private val MapParkColor = Color(0xFFBCD5B5)
private val MapWaterColor = Color(0xFFBFD7E5)
private val MapBlockColor = Color(0xFFE7DFC8)

@Composable
fun MapSearchScreen(
    navController: NavController
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                configuration = BottomBarConfiguration.Search,
                onSearchItemClicked = {},
                onToolsItemClicked = { navController.navigate(NavigationItem.Tools) },
                onNetworkingItemClicked = { navController.navigate(NavigationItem.Networking.Posts) },
                onFavouritesItemClicked = { navController.navigate(NavigationItem.Favourites.Content) }
            )
        }
    ) { paddingValues ->
        MapSearchScreenContent(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun MapSearchScreenContent(
    modifier: Modifier = Modifier,
    categories: List<SearchCategoryChipModel> = SearchSheetMockData.categories,
    places: List<SearchPlaceItemModel> = SearchSheetMockData.places
) {
    var selectedCategoryId by remember {
        mutableStateOf(SearchSheetMockData.CATEGORY_ALL)
    }
    var favoriteIds by remember(places) {
        mutableStateOf(
            places
                .filter(SearchPlaceItemModel::isFavorite)
                .map(SearchPlaceItemModel::id)
                .toSet()
        )
    }

    val filteredPlaces = places
        .map { place -> place.copy(isFavorite = place.id in favoriteIds) }
        .filter { place ->
            selectedCategoryId == SearchSheetMockData.CATEGORY_ALL || place.categoryId == selectedCategoryId
        }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        MockMapBackground(
            modifier = Modifier.fillMaxSize()
        )
        MapPlacesBottomSheet(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.78f),
            categories = categories,
            selectedCategoryId = selectedCategoryId,
            onCategorySelected = { selectedCategoryId = it },
            places = filteredPlaces,
            onFavoriteClick = { placeId ->
                favoriteIds = if (placeId in favoriteIds) {
                    favoriteIds - placeId
                } else {
                    favoriteIds + placeId
                }
            }
        )
    }
}

@Composable
private fun MapPlacesBottomSheet(
    categories: List<SearchCategoryChipModel>,
    selectedCategoryId: String,
    onCategorySelected: (String) -> Unit,
    places: List<SearchPlaceItemModel>,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = SheetContainerColor,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        tonalElevation = 6.dp,
        shadowElevation = 18.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                SheetDragHandle()
            }
            Spacer(modifier = Modifier.height(18.dp))
            CategoryChipsSection(
                categories = categories,
                selectedCategoryId = selectedCategoryId,
                onCategorySelected = onCategorySelected
            )
            Spacer(modifier = Modifier.height(20.dp))
            PlacesList(
                places = places,
                onFavoriteClick = onFavoriteClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MockMapBackground(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(MapBaseColor)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawRoundRect(
                color = MapWaterColor,
                topLeft = Offset(size.width * 0.06f, size.height * 0.10f),
                size = Size(size.width * 0.28f, size.height * 0.16f),
                cornerRadius = CornerRadius(x = 90f, y = 90f)
            )
            drawRoundRect(
                color = MapParkColor,
                topLeft = Offset(size.width * 0.62f, size.height * 0.14f),
                size = Size(size.width * 0.24f, size.height * 0.22f),
                cornerRadius = CornerRadius(x = 80f, y = 80f)
            )
            drawRoundRect(
                color = MapBlockColor,
                topLeft = Offset(size.width * 0.18f, size.height * 0.46f),
                size = Size(size.width * 0.18f, size.height * 0.12f),
                cornerRadius = CornerRadius(x = 32f, y = 32f)
            )
            drawRoundRect(
                color = MapBlockColor,
                topLeft = Offset(size.width * 0.66f, size.height * 0.42f),
                size = Size(size.width * 0.16f, size.height * 0.10f),
                cornerRadius = CornerRadius(x = 28f, y = 28f)
            )

            drawRoad(
                start = Offset(size.width * 0.04f, size.height * 0.24f),
                end = Offset(size.width * 0.94f, size.height * 0.14f)
            )
            drawRoad(
                start = Offset(size.width * 0.10f, size.height * 0.34f),
                end = Offset(size.width * 0.86f, size.height * 0.46f)
            )
            drawRoad(
                start = Offset(size.width * 0.18f, size.height * 0.02f),
                end = Offset(size.width * 0.44f, size.height * 0.58f)
            )
            drawRoad(
                start = Offset(size.width * 0.74f, size.height * 0.04f),
                end = Offset(size.width * 0.52f, size.height * 0.62f)
            )
            drawRoad(
                start = Offset(size.width * 0.02f, size.height * 0.60f),
                end = Offset(size.width * 0.98f, size.height * 0.72f)
            )

            listOf(
                Offset(size.width * 0.32f, size.height * 0.26f),
                Offset(size.width * 0.58f, size.height * 0.30f),
                Offset(size.width * 0.78f, size.height * 0.58f)
            ).forEach { center ->
                drawCircle(
                    color = BasicOrange,
                    radius = 14f,
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f,
                    center = center
                )
            }
        }
    }
}

private fun DrawScope.drawRoad(
    start: Offset,
    end: Offset
) {
    drawLine(
        color = MapRoadColor,
        start = start,
        end = end,
        strokeWidth = 26f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = BasicOrange.copy(alpha = 0.18f),
        start = start,
        end = end,
        strokeWidth = 6f,
        cap = StrokeCap.Round
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MapSearchScreenPreview() {
    AdventureMakerTheme {
        MapSearchScreenContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun MapSearchRoutePreview() {
    AdventureMakerTheme {
        MapSearchScreen(
            navController = rememberNavController()
        )
    }
}
