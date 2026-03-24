package com.softcat.adventuremaker.screens.favourites

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.domain.entities.Place
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.ui.theme.BasicOrange
import com.softcat.adventuremaker.ui.theme.CustomRed
import com.softcat.adventuremaker.ui.theme.LightGray
import org.koin.androidx.compose.koinViewModel

@Composable
@Preview
private fun NoUser(
    modifier: Modifier = Modifier,
    onEnterClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(R.drawable.heart),
                tint = CustomRed,
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.favourites_empty_title),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.favourites_empty_title),
                style = MaterialTheme.typography.bodyLarge,
                color = LightGray,
                textAlign = TextAlign.Center
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = onEnterClick,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = BasicOrange
                )
            ) {
                Text(
                    style = MaterialTheme.typography.labelLarge,
                    text = stringResource(R.string.enter),
                    color = White
                )
            }
        }
    }
}

@Composable
@Preview
private fun CategoryCard(
    modifier: Modifier = Modifier,
    name: String = "Title",
    isActive: Boolean = true,
    onClick: () -> Unit = {}
) {
    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, BasicOrange),
        colors = CardDefaults.cardColors().copy(
            containerColor = if (isActive) BasicOrange else White
        ),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = if (isActive) White else BasicOrange
        )
    }
}

private fun getCategoryNameId(category: Place.Category?) = when (category) {
    Place.Category.Museum -> R.string.museum_category
    Place.Category.Entertainment -> R.string.entertainment_category
    Place.Category.Bank -> R.string.bank_category
    Place.Category.Hotel -> R.string.hotel_category
    Place.Category.Attraction -> R.string.attraction_category
    Place.Category.Restaurant -> R.string.restaurant_category
    else -> R.string.any_category
}

@Composable
@Preview
private fun CategorySelector(
    modifier: Modifier = Modifier,
    onCategoryClicked: (Place.Category?) -> Unit = {},
    category: Place.Category? = null
) {
    val scrollState = rememberScrollState()
    Row(
       modifier = Modifier
           .wrapContentSize()
           .padding(vertical = 8.dp, horizontal = 4.dp)
           .horizontalScroll(scrollState)
           .then(modifier),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryCard(
            modifier = Modifier.wrapContentSize(),
            name = stringResource(getCategoryNameId(null)),
            isActive = category == null,
            onClick = { onCategoryClicked(null) }
        )
        Place.Category.entries.forEach {
            CategoryCard(
                modifier = Modifier.wrapContentSize(),
                name = stringResource(getCategoryNameId(it)),
                isActive = category == it,
                onClick = { onCategoryClicked(it) }
            )
        }
    }
}

@Composable
@Preview
private fun PlaceCard(
    modifier: Modifier = Modifier,
    imageUrl: String = "",
    title: String = "Main title",
    category: Place.Category = Place.Category.Restaurant,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RectangleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                model = imageUrl,
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = stringResource(getCategoryNameId(category)),
                    color = LightGray,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = title,
                    color = White,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
@Preview
private fun Content(
    modifier: Modifier = Modifier,
    onPlaceClick: (Place) -> Unit = {},
    places: List<Place> = emptyList(),
    filterCategory: Place.Category? = null,
    onCategoryClicked: (Place.Category?) -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        CategorySelector(
            onCategoryClicked = onCategoryClicked,
            category = filterCategory
        )
        LazyVerticalGrid(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .then(modifier),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(4.dp)
        ) {
            items(
                items = places,
                key = { it.id }
            ) { place ->
                PlaceCard(
                    modifier = Modifier.height(200.dp).fillMaxWidth(),
                    imageUrl = place.imageUrl,
                    title = place.name,
                    category = place.category,
                    onClick = { onPlaceClick(place) }
                )
            }
        }
    }
}

@Composable
private fun Loading(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier.fillMaxSize().then(modifier),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 5.dp,
            strokeCap = StrokeCap.Butt
        )
    }
}

@Composable
fun FavouritesContent(
    navController: NavController,
    viewModel: FavouriteViewModel = koinViewModel(),
    onEnterClick: () -> Unit,
    onPlaceClick: (Place) -> Unit
) {
    val state = viewModel.state.observeAsState(FavouriteState.Loading)

    Scaffold(
        topBar = { FavouritesAppBar(onEnterClick) },
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.Favourites,
                onSearchItemClicked = { navController.navigate(NavigationItem.Search.Map) },
                onToolsItemClicked = { navController.navigate(NavigationItem.Tools) },
                onNetworkingItemClicked = { navController.navigate(NavigationItem.Networking.Posts) },
                onFavouritesItemClicked = { navController.navigate(NavigationItem.Favourites.Content) }
            )
        }
    ) { paddingValues ->
        when (val currentState = state.value) {

            is FavouriteState.Content -> {
                Content(
                    modifier = Modifier.padding(paddingValues),
                    onPlaceClick = onPlaceClick,
                    places = currentState.places.filter {
                        currentState.filterCategory == null || it.category == currentState.filterCategory
                    },
                    filterCategory = currentState.filterCategory,
                    onCategoryClicked = { viewModel.changeCategoryFilter(it) }
                )
            }

            FavouriteState.Loading -> {
                Loading(Modifier.padding(paddingValues))
            }

            FavouriteState.NoUser -> NoUser(
                modifier = Modifier.padding(paddingValues),
                onEnterClick = onEnterClick
            )
        }
    }
}