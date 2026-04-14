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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
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
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem.BottomBarConfiguration
import com.softcat.adventuremaker.screens.search.components.CategoryChipsSection
import com.softcat.adventuremaker.screens.search.components.PlacesList
import com.softcat.adventuremaker.ui.components.SheetDragHandle
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import com.softcat.adventuremaker.ui.theme.BasicOrange
import com.softcat.adventuremaker.ui.theme.GradientGreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun MapSearchScreen(
    navController: NavController
) {
    val viewModel: SearchViewModel = koinViewModel()
    val state = viewModel.state.observeAsState(viewModel.initialSearchScreenState())
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                configuration = BottomBarConfiguration.Search,
                navController = navController
            )
        },
        floatingActionButton = {
            OpenBottomSheetButton(
                onClick = viewModel::showBottomSheet
            )
        }
    ) { paddingValues ->
        MapSearchScreenContent(
            modifier = Modifier.padding(paddingValues),
            onChangeFavouriteStatusClick = viewModel::changeFavouriteStatus,
            onDismiss = viewModel::dismissBottomSheet,
            onCategorySelected = viewModel::selectCategory,
            onPlaceClicked = { placeId ->
                viewModel.navigateToPlaceDetails(navController, placeId)
            },
            state = state.value.bottomSheetState,
        )
    }
}

@Composable
@Preview
private fun OpenBottomSheetButton(
    modifier: Modifier = Modifier,
    isBottomSheetVisible: Boolean = false,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        modifier = Modifier
            .wrapContentSize()
            .then(modifier),
        onClick = onClick,
        containerColor = GradientGreen,
        shape = CircleShape
    ) {
        val img = if (isBottomSheetVisible) Icons.Default.Add else Icons.Default.ArrowDropDown
        Icon(
            modifier = Modifier.size(64.dp).padding(6.dp),
            imageVector = img,
            contentDescription = null
        )
    }
}

@Composable
private fun MapSearchScreenContent(
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
    val mapBaseColor = Color(0xFFD8E2D4)
    val mapParkColor = Color(0xFFBCD5B5)
    val mapWaterColor = Color(0xFFBFD7E5)
    val mapBlockColor = Color(0xFFE7DFC8)
    Box(
        modifier = modifier.background(mapBaseColor)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawRoundRect(
                color = mapWaterColor,
                topLeft = Offset(size.width * 0.06f, size.height * 0.10f),
                size = Size(size.width * 0.28f, size.height * 0.16f),
                cornerRadius = CornerRadius(x = 90f, y = 90f)
            )
            drawRoundRect(
                color = mapParkColor,
                topLeft = Offset(size.width * 0.62f, size.height * 0.14f),
                size = Size(size.width * 0.24f, size.height * 0.22f),
                cornerRadius = CornerRadius(x = 80f, y = 80f)
            )
            drawRoundRect(
                color = mapBlockColor,
                topLeft = Offset(size.width * 0.18f, size.height * 0.46f),
                size = Size(size.width * 0.18f, size.height * 0.12f),
                cornerRadius = CornerRadius(x = 32f, y = 32f)
            )
            drawRoundRect(
                color = mapBlockColor,
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
    val mapRoadColor = Color(0xFFFDFBF5)
    drawLine(
        color = mapRoadColor,
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
