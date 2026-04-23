package com.softcat.adventuremaker.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.domain.entities.Post
import com.example.domain.entities.User
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.ui.theme.BasicOrange
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.screens.details.getAddressFromCoordinates
import com.softcat.adventuremaker.ui.theme.AvatarPlaceholder
import com.softcat.adventuremaker.ui.theme.GradientGreen
import com.softcat.adventuremaker.ui.theme.StarYellow
import com.softcat.adventuremaker.ui.theme.TextGray
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import com.softcat.adventuremaker.screens.auth.SecondaryButton
import org.koin.androidx.compose.koinViewModel


@Composable
fun ProfileContent(
    navController: NavController,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState(ProfileState.Loading)

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.None,
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(NavigationItem.Networking.CreatePost) },
                containerColor = GradientGreen
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
    ) { padding ->
        when (val currentState = state) {
            ProfileState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            ProfileState.NoUser -> {
                LaunchedEffect(Unit) {
                    navController.navigate(NavigationItem.Auth) {
                        popUpTo(0) // очистка стека
                    }
                }
            }

            is ProfileState.Content -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    item {
                        ProfileHeader(
                            user = currentState.user,
                            onLogoutClick = viewModel::logout,
                            onAvatarClick = {}
                        )
                    }
                    item {
                        Text(
                            text = stringResource(R.string.posts_title),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    if (currentState.posts.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.posts_empty),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    } else {
                        items(
                            items = currentState.posts,
                            key = { it.id }
                        ) { post ->
                            PostItem(post = post, user = currentState.user)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileHeader(
    user: User,
    onLogoutClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box {

        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(BasicOrange, GradientGreen)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Аватар
            if (user.avatarUrl != null) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .clickable(onClick = onAvatarClick),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(AvatarPlaceholder, CircleShape)
                        .clickable(onClick = onAvatarClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(user.name)

            Spacer(Modifier.height(12.dp))

            SecondaryButton(stringResource(R.string.log_out), onClick = onLogoutClick, modifier = Modifier.padding(horizontal = 8.dp))
        }
    }
}


@Composable
fun PostItem(
    post: Post,
    user: User,
    modifier: Modifier = Modifier
) {

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
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Аватар
            if (user.avatarUrl != null) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(TextGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null)
                }
            }

            Spacer(Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(user.name)
                Text(
                    text = address ?: stringResource(R.string.loading),
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(Icons.Default.MoreVert, null)
        }

        Spacer(Modifier.height(4.dp))

        AsyncImage(
            model = post.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = post.description,
                modifier = Modifier.weight(1f)
            )

            Row {
                repeat(post.scoreValue ?: 0) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = StarYellow // красивый жёлтый
                    )
                }
            }
        }
    }
}