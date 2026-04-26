package com.softcat.adventuremaker.screens.feed

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.domain.entities.Post
import com.google.android.gms.location.LocationServices
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.screens.details.getAddressFromCoordinates
import com.softcat.adventuremaker.screens.posts.PostsState
import com.softcat.adventuremaker.ui.theme.*
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedTopBar(onProfileClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.navigation_networking_label),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = Black
            )
        },
        actions = {
            IconButton(onClick = onProfileClick) {
                Icon(Icons.Default.Person, contentDescription = null, tint = BasicIconsTint)
            }
        }
    )
}

@Composable
private fun FeedItem(post: Post) {
    val context = LocalContext.current
    var address by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(post.latitude, post.longitude) {
        getAddressFromCoordinates(
            context,
            post.latitude.toDouble(),
            post.longitude.toDouble()
        ) { address = it }
    }

    Column(Modifier.fillMaxWidth().padding(12.dp)) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            Box(
                Modifier.size(52.dp).background(AvatarPlaceholder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = White)
            }

            Column(Modifier.padding(start = 8.dp).weight(1f)) {
                Text(post.userId, color = Black)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Text(address ?: stringResource(R.string.loading), color = TextGray)
                }
            }
        }

        AsyncImage(
            model = post.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(260.dp).padding(top = 8.dp),
            contentScale = ContentScale.Crop
        )

        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(post.description, modifier = Modifier.weight(1f))

            Row {
                repeat(post.scoreValue ?: 0) {
                    Icon(Icons.Default.Star, null, tint = StarYellow)
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

    // чтобы не вызывать загрузку несколько раз
    var isStarted by remember { mutableStateOf(false) }

    fun startLoading() {
        if (isStarted) return
        isStarted = true

        getCurrentLocation(context) { lat, lon ->
            viewModel.loadPosts(lat.toFloat(), lon.toFloat())
        }
    }

    // permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startLoading()
        } else {
            // fallback
            viewModel.loadPosts(55.7558f, 37.6173f)
        }
    }

    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            startLoading()
        } else {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    Scaffold(
        topBar = {
            FeedTopBar {
                navController.navigate(NavigationItem.Networking.Profile)
            }
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
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->

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
                val msg = (state as PostsState.Error).message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("${stringResource(R.string.error)}: $msg")
                }
            }

            is PostsState.Content -> {
                val posts = (state as PostsState.Content).posts

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding)
                ) {
                    items(posts, key = { it.id }) {
                        FeedItem(it)
                    }
                }
            }
        }
    }
}


@SuppressLint("MissingPermission")
fun getCurrentLocation(
    context: Context,
    onResult: (Double, Double) -> Unit
) {
    val client = LocationServices.getFusedLocationProviderClient(context)

    client.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onResult(location.latitude, location.longitude)
            } else {
                onResult(55.7558, 37.6173)
            }
        }
        .addOnFailureListener {
            onResult(55.7558, 37.6173)
        }
        .addOnCanceledListener {
            onResult(55.7558, 37.6173)
        }
}