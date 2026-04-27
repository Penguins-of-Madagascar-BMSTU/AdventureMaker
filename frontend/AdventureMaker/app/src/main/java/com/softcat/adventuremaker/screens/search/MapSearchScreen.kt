package com.softcat.adventuremaker.screens.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.domain.entities.Place
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.screens.search.components.CategoryChipsSection
import com.softcat.adventuremaker.screens.search.components.OpenStreetMapView
import com.softcat.adventuremaker.screens.search.components.PlacesList
import com.softcat.adventuremaker.screens.search.model.SearchScreenState
import com.softcat.adventuremaker.ui.components.SheetDragHandle
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
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
        floatingActionButton = {
            OpenBottomSheetButton(
                onClick = viewModel::showBottomSheet,
                isBottomSheetVisible = state.value.bottomSheetState.isSheetVisible
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
            onQueryChange = viewModel::changeQuery,
            onQueryEntered = viewModel::searchPlaces,
            onCitySelected = viewModel::selectCity,
            state = state.value,
        )
    }
}

@Composable
private fun SearchLine(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onTextEntered: ()-> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = text,
        leadingIcon = {
            Icon(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(48.dp)
                    .padding(vertical = 10.dp),
                painter = painterResource(R.drawable.search),
                contentDescription = null,
                tint = BasicIconsTint
            )
        },
        onValueChange = onTextChange,
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Black,
            unfocusedTextColor = Black,
            unfocusedContainerColor = White,
            focusedContainerColor = White,
            focusedBorderColor = BasicOrange,
            unfocusedBorderColor = White,
        ),
        textStyle = MaterialTheme.typography.labelLarge,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = { onTextEntered() }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CitySelector(
    modifier: Modifier = Modifier,
    availableCities: List<String>,
    selectedCity: String,
    onCitySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedCity,
            onValueChange = {},
            placeholder = {
                Text(
                    text = stringResource(R.string.select_city),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black
                )
            },
            readOnly = true,
            modifier = Modifier
                .heightIn(max = 128.dp)
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BasicOrange,
                unfocusedBorderColor = BasicOrange,
                focusedContainerColor = White,
                unfocusedContainerColor = White,
            ),
            shape = RoundedCornerShape(50),
            maxLines = 1
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            availableCities.forEach { city ->
                DropdownMenuItem(
                    text = {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            text = city,
                            color = Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun OpenBottomSheetButton(
    modifier: Modifier = Modifier,
    isBottomSheetVisible: Boolean,
    onClick: () -> Unit
) {
    if (isBottomSheetVisible)
        return
    FloatingActionButton(
        modifier = Modifier
            .wrapContentSize()
            .then(modifier),
        onClick = onClick,
        containerColor = GradientGreen,
        shape = CircleShape
    ) {
        Icon(
            modifier = Modifier.size(64.dp).padding(8.dp),
            imageVector = Icons.Default.Add,
            contentDescription = null
        )
    }
}

@Composable
private fun MapSearchScreenContent(
    modifier: Modifier = Modifier,
    state: SearchScreenState,
    onChangeFavouriteStatusClick: (String) -> Unit,
    onDismiss: () -> Unit,
    onCategorySelected: (Place.Category) -> Unit,
    onPlaceClicked: (String) -> Unit,
    onQueryChange: (String) -> Unit,
    onQueryEntered: () -> Unit,
    onCitySelected: (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        OpenStreetMapView(
            modifier = Modifier.fillMaxSize(),
            state = state.mapState
        )
        MapPlacesBottomSheet(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight(),
            state = state.bottomSheetState,
            onChangeFavouriteStatusClick = onChangeFavouriteStatusClick,
            onDismiss = onDismiss,
            onCategorySelected = onCategorySelected,
            onPlaceClicked = onPlaceClicked,
        )
        SearchLine(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 8.dp, end = 4.dp),
            text = state.query,
            onTextChange = onQueryChange,
            onTextEntered = onQueryEntered
        )
        CitySelector(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.4f)
                .padding(end = 16.dp, top = 8.dp),
            availableCities = state.availableCities,
            selectedCity = state.cityName,
            onCitySelected = onCitySelected
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
                    modifier = Modifier.fillMaxHeight(if (state.places.isNotEmpty()) 1f else 0f)
                )
            }
        }
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
