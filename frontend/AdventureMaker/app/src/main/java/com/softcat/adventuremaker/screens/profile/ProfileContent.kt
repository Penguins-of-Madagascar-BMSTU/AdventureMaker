package com.softcat.adventuremaker.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.ui.theme.AvatarPlaceholder
import com.softcat.adventuremaker.ui.theme.GradientGreen
import com.softcat.adventuremaker.ui.theme.StarYellow
import com.softcat.adventuremaker.ui.theme.TextGray


@Composable
fun ProfileContent(navController: NavController) {

    // мок - данные
    val user = remember {
        User(
            id = "1",
            name = "Антон",
            email = "test@mail.com",
            avatarUrl = "https://i.pravatar.cc/150?img=6"
        )
    }

    val posts = remember {
        listOf(
            Post(
                id = "1",
                userId = "1",
                imageUrl = "https://i.pravatar.cc/350?img=29",
                scoreValue = 5,
                description = "Шикарное место! Всем рекомендую!",
                latitude = 55.75f,
                longitude = 37.61f
            )
        )
    }


    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.Networking,
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {}, // сюда добавить добавление поста
                containerColor = GradientGreen
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            item {
                ProfileHeader(user)
            }

            item {
                Text(
                    text = stringResource(R.string.posts_title),
                    modifier = Modifier.padding(12.dp)
                )
            }

            items(posts) {
                PostItem(it, user)
            }
        }
    }
}


@Composable
fun ProfileHeader(
    user: User,
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
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(AvatarPlaceholder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(user.name)
        }
    }
}


@Composable
fun PostItem(
    post: Post,
    user: User,
    modifier: Modifier = Modifier
) {

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
                    "Москва, Зарядье", // мок!!!
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