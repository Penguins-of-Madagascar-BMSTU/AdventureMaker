package com.softcat.adventuremaker.screens.favourites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.domain.entities.FavouriteScreenVariant
import com.example.domain.entities.Place
import com.softcat.adventuremaker.ui.theme.LightGray

@Composable
fun FavouriteList(
    modifier: Modifier= Modifier,
    places: List<Place>,
    onPlaceClick: (Place) -> Unit,
    variant: FavouriteScreenVariant
) {
    if (variant == FavouriteScreenVariant.First) {
        FavouritesListV1(
            modifier = modifier,
            places = places,
            onPlaceClick = onPlaceClick
        )
    } else {
        FavouritesListV2(
            modifier = modifier,
            places = places,
            onPlaceClick = onPlaceClick
        )
    }
}

@Composable
private fun FavouritesListV1(
    modifier: Modifier= Modifier,
    places: List<Place>,
    onPlaceClick: (Place) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2)
    ) {
        items(
            items = places,
            key = { it.id }
        ) { place ->
            PlaceCard(
                modifier = Modifier.height(200.dp).fillMaxWidth().padding(8.dp),
                imageUrl = place.imageUrls.firstOrNull() ?: "",
                title = place.name,
                category = place.category,
                onClick = { onPlaceClick(place) }
            )
        }
    }
}

@Composable
private fun FavouritesListV2(
    modifier: Modifier = Modifier,
    places: List<Place>,
    onPlaceClick: (Place) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = places,
            key = { it.id }
        ) { place ->
            PlaceItem(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                title = place.name,
                category = place.category,
                description = place.description,
                onClick = { onPlaceClick(place) }
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
private fun PlaceItem(
    modifier: Modifier = Modifier,
    title: String,
    category: Place.Category,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        shape = RectangleShape,
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                style = MaterialTheme.typography.labelLarge,
                text = title,
                color = Black,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                style = MaterialTheme.typography.labelLarge,
                text = stringResource(getCategoryNameId(category)),
                color = LightGray,
                maxLines = 1
            )
            Text(
                modifier = Modifier.heightIn(max = 128.dp),
                style = MaterialTheme.typography.bodyMedium,
                text = description,
                color = LightGray
            )
        }
    }
}