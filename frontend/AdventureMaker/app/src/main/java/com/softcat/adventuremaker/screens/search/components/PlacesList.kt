package com.softcat.adventuremaker.screens.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.screens.search.PlaceItemModel
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.CustomRed
import com.softcat.adventuremaker.ui.theme.LightGray

@Composable
fun PlacesList(
    places: List<PlaceItemModel>,
    onChangeFavouriteStatusClick: (String) -> Unit,
    onPlaceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(LightGray),
        verticalArrangement = Arrangement.spacedBy(1.dp),
    ) {
        items(
            items = places,
            key = { place -> place.id }
        ) { place ->
            PlaceItem(
                place = place,
                onFavouriteClick = { onChangeFavouriteStatusClick(place.id) },
                onPlaceClick = onPlaceClick
            )
        }
    }
}

@Composable
fun PlaceItem(
    place: PlaceItemModel,
    onFavouriteClick: () -> Unit,
    onPlaceClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .then(modifier),
        onClick = { onPlaceClick(place.id) },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .wrapContentHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AsyncImage(
                model = place.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LightGray),
                contentScale = ContentScale.Crop
            )
            Text(
                modifier = Modifier.weight(1f),
                text = place.title,
                style = MaterialTheme.typography.titleMedium,
                color = BasicIconsTint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onFavouriteClick) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(place.iconResId),
                    contentDescription = null,
                    tint = CustomRed
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceItemPreview() {
    val place = remember {
        PlaceItemModel(
            id ="1",
            title = "Кофейня cup2up",
            imageUrl = "",
            iconResId = R.drawable.heart_filled
        )
    }
}