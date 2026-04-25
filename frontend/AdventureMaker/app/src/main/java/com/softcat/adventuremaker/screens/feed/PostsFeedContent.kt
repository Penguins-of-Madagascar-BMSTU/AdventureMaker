package com.softcat.adventuremaker.screens.feed

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.domain.entities.Post
import com.google.android.gms.location.LocationServices
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.screens.details.getAddressFromCoordinates
import com.softcat.adventuremaker.screens.posts.PostsState
import com.softcat.adventuremaker.ui.theme.AvatarPlaceholder
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.GradientGreen
import com.softcat.adventuremaker.ui.theme.StarYellow
import com.softcat.adventuremaker.ui.theme.TextGray
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedTopBar(
    onProfileClick: () -> Unit
) {
    TopAppBar(
        expandedHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight,
        windowInsets = TopAppBarDefaults.windowInsets,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.navigation_networking_label),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = Black
            )
        },
        actions = {
            IconButton(
                onClick = onProfileClick
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.Person,
                    tint = BasicIconsTint,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun FeedItem(post: Post) {

    // получение адреса по координатам
    val context = LocalContext.current
    var address by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(post.latitude, post.longitude) {
        getAddressFromCoordinates(
            context = context,
            latitude = post.latitude.toDouble(),
            longitude = post.longitude.toDouble()
        ) { result ->
            address = result
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(AvatarPlaceholder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = White)
            }
            Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {
                Text(post.userId, style = MaterialTheme.typography.titleMedium, color = Black)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = address ?: stringResource(R.string.loading),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextGray
                    )
                }
            }
        }

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(260.dp),
            model = post.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = post.description,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleMedium,
                color = Black
            )
            Row {
                repeat(post.scoreValue ?: 0) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StarYellow
                    )
                }
            }
        }
    }
}

@Composable
fun PostsFeedContent(navController: NavController) {

    val viewModel: PostsViewModel = koinViewModel()
    val state by viewModel.state.observeAsState(PostsState.Loading)

    val context = LocalContext.current

    // permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            if (granted) {
                getCurrentLocation(context) { lat, lon ->
                    viewModel.loadPosts(lat.toFloat(), lon.toFloat())
                }
            }
        }
    )

    // запрос permission
    LaunchedEffect(Unit) {
        permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    Scaffold(
        topBar = {
            FeedTopBar(
                onProfileClick = {
                    navController.navigate(NavigationItem.Networking.Profile)
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.Networking,
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = GradientGreen,
                onClick = {
                    navController.navigate(NavigationItem.Networking.CreatePost)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        when (state) {

            is PostsState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.loading))
                }
            }

            is PostsState.Empty -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_posts))
                }
            }

            is PostsState.Error -> {
                val message = (state as PostsState.Error).message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("${stringResource(R.string.error)}: $message")
                }
            }

            is PostsState.Content -> {
                val posts = (state as PostsState.Content).posts

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    items(posts, key = { it.id }) { post ->
                        FeedItem(post)
                    }
                }
            }
        }
    }
}


// получение текущих координат пользователя
@SuppressLint("MissingPermission")
fun getCurrentLocation(context: Context, onResult: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            }
            else
            {
                onResult(55.7558, 37.6173) // по умолчанию вы в Москве)
            }
        }
}