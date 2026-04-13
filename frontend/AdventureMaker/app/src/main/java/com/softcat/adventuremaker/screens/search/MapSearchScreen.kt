package com.softcat.adventuremaker.screens.search

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.domain.entities.Place
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem.BottomBarConfiguration
import com.softcat.adventuremaker.screens.search.components.CategoryChipsSection
import com.softcat.adventuremaker.screens.search.components.PlacesList
import com.softcat.adventuremaker.ui.components.SheetDragHandle
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import com.softcat.adventuremaker.ui.theme.BasicOrange
import kotlinx.coroutines.launch

private val MapBaseColor = Color(0xFFD8E2D4)
private val MapRoadColor = Color(0xFFFDFBF5)
private val MapParkColor = Color(0xFFBCD5B5)
private val MapWaterColor = Color(0xFFBFD7E5)
private val MapBlockColor = Color(0xFFE7DFC8)

@Composable
fun MapSearchScreen(
    navController: NavController
) {
    val state = remember {
        SearchBottomSheetState(
            places = listOf(
                PlaceItemModel(
                    id = "1",
                    title = "Пруд с уточками",
                    imageUrl = "",
                    iconResId = R.drawable.heart_filled
                ),
                PlaceItemModel(
                    id = "2",
                    title = "Кофейня cup2cup",
                    imageUrl = "",
                    iconResId = R.drawable.heart
                ),
                PlaceItemModel(
                    id = "3",
                    title = "Гостиница в центре города",
                    imageUrl = "",
                    iconResId = R.drawable.heart_filled
                )
            ),
            categories = listOf(
                SearchCategoryModel(
                    key = Place.Category.Unknown,
                    titleResId = R.string.search_sheet_category_all,
                    isSelected = true
                ),
                SearchCategoryModel(
                    key = Place.Category.Entertainment,
                    titleResId = R.string.entertainment_category,
                    isSelected = false
                ),
                SearchCategoryModel(
                    key = Place.Category.Restaurant,
                    titleResId = R.string.restaurant_category,
                    isSelected = false
                ),
                SearchCategoryModel(
                    key = Place.Category.Attraction,
                    titleResId = R.string.attraction_category,
                    isSelected = false
                ),
                SearchCategoryModel(
                    key = Place.Category.Hotel,
                    titleResId = R.string.hotel_category,
                    isSelected = false
                ),
                SearchCategoryModel(
                    key = Place.Category.Bank,
                    titleResId = R.string.bank_category,
                    isSelected = false
                )
            ),
            isSheetVisible = true
        )
    }
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                configuration = BottomBarConfiguration.Search,
                navController = navController
            )
        }
    ) { paddingValues ->
        MapSearchScreenContent(
            modifier = Modifier.padding(paddingValues),
            onChangeFavouriteStatusClick = {},
            onDismiss = {},
            onCategorySelected = {},
            onPlaceClicked = {},
            state = state,
        )
    }
}

@Composable
fun MapSearchScreenContent(
    modifier: Modifier = Modifier,
    state: SearchBottomSheetState,
    onChangeFavouriteStatusClick: (String) -> Unit,
    onDismiss: () -> Unit,
    onCategorySelected: (Place.Category) -> Unit,
    onPlaceClicked: (String) -> Unit,
) {
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
                .wrapContentHeight(),
            state = state,
            onChangeFavouriteStatusClick = onChangeFavouriteStatusClick,
            onDismiss = onDismiss,
            onCategorySelected = onCategorySelected,
            onPlaceClicked = onPlaceClicked,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapPlacesBottomSheet(
    state: SearchBottomSheetState,
    onChangeFavouriteStatusClick: (String) -> Unit,
    onDismiss: () -> Unit,
    onCategorySelected: (Place.Category) -> Unit,
    onPlaceClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val scope = rememberCoroutineScope()
    if (state.isSheetVisible) {
        ModalBottomSheet(
            modifier = modifier,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            tonalElevation = 10.dp,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            },
            dragHandle = {
                SheetDragHandle(
                    modifier = Modifier.padding(top = 8.dp)
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            scrimColor = Color.Transparent,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                CategoryChipsSection(
                    categories = state.categories,
                    onCategorySelected = onCategorySelected
                )
                Spacer(modifier = Modifier.height(32.dp))
                PlacesList(
                    places = state.places,
                    onChangeFavouriteStatusClick = onChangeFavouriteStatusClick,
                    onPlaceClick = onPlaceClicked,
                    modifier = Modifier.heightIn(max = 256.dp)
                )
            }
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

@Preview(showBackground = true)
@Composable
private fun MapSearchRoutePreview() {
    AdventureMakerTheme {
        MapSearchScreen(
            navController = rememberNavController()
        )
    }
}
