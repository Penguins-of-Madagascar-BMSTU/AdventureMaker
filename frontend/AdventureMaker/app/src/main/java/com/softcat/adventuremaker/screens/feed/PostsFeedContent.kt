package com.softcat.adventuremaker.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.domain.entities.Post
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.ui.theme.AvatarPlaceholder
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.GradientGreen
import com.softcat.adventuremaker.ui.theme.StarYellow
import com.softcat.adventuremaker.ui.theme.TextGray

private val mockPosts = listOf(
    Post(
        id = "1",
        userId = "Дарья",
        imageUrl = "https://yandex-images.clstorage.net/iXe5d3139/6f4159Q-K/StD2gciEYMCzchyGzYa84kXmMnAgF6UGOYi-FIcTlOV3IamOVlFA_KbQPW6mqcv7_5vLce9NW83kG-uTJdUH4wfBr5iZrRncs2ZNquS21NIFswIFLEKtYimV2iSKtjkglsgQdo-wuRVasXQP8qzm0uvCY5m_WB6kYP5ae2XQjqrmzD7B2Bx59KvS7YbN1qAaNSO20NT-pQPxbCu1Dp5RfJpESGaTcJUBisHgJzIIW86PkksooygOpKTKlsMZTHIWSqTe1RRgfTDnCjkurZrcUpVPInwIGhV3leRj3QbaDTTWxFCiG5BpIfIBOAMu_DNm89bP0J-ADphYej7blTz6Pn6oVgF4RCHIw1L5AgFOtKJtgy6EVF4x-4XsM0VOav2YChzIkg_YATmueaiXomyT2j86l4z7nA4gzJbam714fqZ6oGLh3PQZ3AO-gfaFVrTGTYM6HGzugbt9CBM5bp5RYPYghFoXTBHh2g0oF_aAv_7_Wl9g95RCXBCKQgPppK4KVuy6MXTcZewvdkUqPTJgxknTbpQokn1_iYg3AeIG1YSmVLQO2wSh0TaBtPfSABd-x9rDIBdAusBAfkK3GSBy-uZUYnVc9PUYA_YBBokSrCLp274oHM7xE63Mty0mQt00vtjcZl_QHTl2sdjjArynop-Ch0zjXLokqFYWM320OlIK1CKJ0Aw97DNOTYpBxlTeYceyQHzCwSeJpCMBIupBQC5kKHrvWBmF9pFcj4ocy9L_st9sL1TOJDgiSuN5NAo-6sCWCXzYeSQfImkCnWawptlHaiDkZpVjjXQj_QJWmeimACzac8zh4XbZIB-iqLtCSwKz7IfUBlBgfj7LGSwi-obs3gFgCPXIX8JFEqEaWEINn9pMCPa9a_F8Ezne4qE4mgDI8muQQQ0-vcjz0vBTbj-CV7g7fC4g5Crq7410LlJWvD5thFiBJNcu1WY5Lkwy1dPWTGwONbfR6AMJbqJk",
        scoreValue = 5,
        description = "Шикарное место, всем советую!!!",
        latitude = 0f,
        longitude = 0f
    ),
    Post(
        id = "2",
        userId = "Дарья",
        imageUrl = "https://i.pinimg.com/474x/dd/05/d7/dd05d7148472d861d380a8723436cd0c.jpg?nii=t",
        scoreValue = 4,
        description = "Еще одно классное место",
        latitude = 0f,
        longitude = 0f
    ),
    Post(
        id = "3",
        userId = "Мария",
        imageUrl = "https://i.pinimg.com/474x/dd/05/d7/dd05d7148472d861d380a8723436cd0c.jpg?nii=t",
        scoreValue = 5,
        description = "неплохое место с красивым видом, можно сделать крутые фотографии",
        latitude = 0f,
        longitude = 0f
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeedTopBar() {
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
            IconButton(onClick = {}) {
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
                        text = "Зарядье",
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
fun PostsFeedContent(
    navController: NavController
) {
    Scaffold(
        topBar = { FeedTopBar() },
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.Networking,
                navController = navController
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = GradientGreen,
                onClick = {}
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Top
        ) {
            items(mockPosts, key = { it.id }) { post ->
                FeedItem(post)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedItemPreview() {
    FeedItem(post = mockPosts.first())
}

@Preview(showBackground = true)
@Composable
private fun PostsFeedContentPreview() {
    PostsFeedContent(navController = rememberNavController())
}
