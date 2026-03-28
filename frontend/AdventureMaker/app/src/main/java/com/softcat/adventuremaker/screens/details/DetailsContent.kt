package com.softcat.adventuremaker.screens.details

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.BuildConfig
import com.example.domain.entities.Place
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale

private fun getCategoryNameId(category: Place.Category) = when (category) {
    Place.Category.Museum -> R.string.museum_category
    Place.Category.Entertainment -> R.string.entertainment_category
    Place.Category.Bank -> R.string.bank_category
    Place.Category.Hotel -> R.string.hotel_category
    Place.Category.Attraction -> R.string.attraction_category
    Place.Category.Restaurant -> R.string.restaurant_category
}

private fun getMapImageUrl(latitude: Float, longitude: Float, size: IntSize): String {
    if (size.height == 0 || size.width == 0)
        return ""
    val link = "https://static.maps.2gis.com/2.0"
    val sizeArg = "${size.width / 2}x${size.height / 2}"
    val args = "?s=$sizeArg&c=$latitude,$longitude&z=14&pt=$latitude,$longitude"
    val key = "&key=${BuildConfig.API_KEY_2GIS}"
    return link + args + key
}

@Composable
@Preview
private fun PlaceDescription(
    modifier: Modifier = Modifier,
    name: String = "Place title",
    category: Place.Category = Place.Category.Restaurant,
    description: String = "An interesting place where u can have some fun."
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.padding(10.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            color = BasicIconsTint,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(getCategoryNameId(category)),
            style = MaterialTheme.typography.bodyLarge,
            color = BasicIconsTint
        )
        Spacer(Modifier.height(32.dp))
        Text(
            modifier = Modifier.verticalScroll(scrollState),
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = BasicIconsTint
        )
    }
}

fun getAddressFromCoordinates(
    context: Context,
    latitude: Double,
    longitude: Double,
    onResultCallback: (String) -> Unit
) {
    val geocoder = Geocoder(context, Locale.getDefault())
    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
        val address = addresses.firstOrNull()?.getAddressLine(0)
        address?.let { onResultCallback(it) }
    }
}

@Composable
fun PlaceOnMap(
    modifier: Modifier = Modifier,
    latitude: Float,
    longitude: Float
) {
    var addressLineState by remember { mutableStateOf("") }
    getAddressFromCoordinates(
        context = LocalContext.current,
        latitude = latitude.toDouble(),
        longitude = longitude.toDouble(),
        onResultCallback = { addressLineState = it }
    )
    var mapImageSize by remember { mutableStateOf(IntSize.Zero) }

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        if (addressLineState.isNotEmpty()) {
            Row(
                modifier = Modifier.wrapContentSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(R.drawable.location),
                    tint = BasicIconsTint,
                    contentDescription = null
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = addressLineState,
                    color = BasicIconsTint,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(20)
        ) {
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged { mapImageSize = it },
                model = getMapImageUrl(latitude, longitude, mapImageSize),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Composable
fun DetailsContent(
    place: Place,
    navController: NavController
) {
    val viewModel: PlaceDetailsViewModel = koinViewModel { parametersOf(place) }
    val state by viewModel.state.observeAsState(PlaceDetailsState(null, place))

    Scaffold(
        topBar = {
            DetailsAppBar(
                isFavourite = state.isFavourite,
                changeFavouriteStatus = viewModel::changeFavouriteStatus,
                onBackClicked = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.Favourites,
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxWidth().weight(1f),
                model = state.place.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            PlaceDescription(
                modifier = Modifier.fillMaxWidth().weight(1f),
                name = state.place.name,
                category = state.place.category,
                description = state.place.description
            )
            PlaceOnMap(
                modifier = Modifier.fillMaxWidth().weight(1f),
                latitude = place.latitude,
                longitude = place.longitude
            )
        }
    }
}