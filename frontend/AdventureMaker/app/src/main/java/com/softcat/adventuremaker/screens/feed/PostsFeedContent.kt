package com.softcat.adventuremaker.screens.feed

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.designElements.PostItem
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.GradientGreen
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.util.GeoPoint


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
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun PostsFeedContent(navController: NavController) {
    val viewModel: PostsViewModel = koinViewModel()
    val state by viewModel.state.observeAsState(PostsState())
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult = viewModel::startLoading
    )

    LaunchedEffect(Unit) {
        val permission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (permission == PackageManager.PERMISSION_GRANTED) {
            viewModel.startLoading(true)
        } else {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        topBar = {
            FeedTopBar {
                navController.navigate(NavigationItem.Networking.Auth)
            }
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = GradientGreen,
                onClick = {
                    state.userId?.let {
                        val entry = NavigationItem.Networking.CreatePost(it)
                        navController.navigate(entry)
                    }
                }
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { paddingValues ->
        StateContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            state = state,
            loadNextPosts = viewModel::loadMore
        )
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PostsEvent.Error -> snackBarHostState.showSnackbar(event.msg)
            }
        }
    }
}

@Composable
private fun StateContent(
    modifier: Modifier = Modifier,
    state: PostsState,
    loadNextPosts: () -> Unit
) {
    if (state.isLoading) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.loading))
        }
    } else if (state.posts.isEmpty() && !state.hasNext) {
        Box(modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.no_posts))
        }
    } else {
        LazyColumn(modifier) {
            items(state.posts, key = { it.id }) {
                PostItem(it)
            }
            loadNextSideEffect(
                modifier =  Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .wrapContentHeight(),
                hasNext = state.hasNext,
                loadNext = loadNextPosts
            )
        }
    }
}

private fun LazyListScope.loadNextSideEffect(
    modifier: Modifier = Modifier,
    hasNext: Boolean,
    loadNext: () -> Unit
) {
    if (hasNext) {
        item {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(Modifier.size(48.dp))
            }
            SideEffect {
                loadNext()
            }
        }
    }
}