package com.softcat.adventuremaker.designElements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.designElements.models.PostModel
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.LightGray
import com.softcat.adventuremaker.ui.theme.Purple80
import com.softcat.adventuremaker.ui.theme.StarYellow
import com.softcat.adventuremaker.ui.theme.TextGray

@Composable
fun PostItem(
    post: PostModel,
    modifier: Modifier = Modifier,
    onDeleteRequest: (() -> Unit)? = null,
    placeholder: Painter? = null,
    avatarPlaceholder: Painter? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        PostHead(
            post = post,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            onDeleteRequest = onDeleteRequest,
            avatarPlaceholder = avatarPlaceholder
        )
        Spacer(Modifier.height(4.dp))
        AsyncImage(
            model = post.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop,
            placeholder = placeholder
        )
        Spacer(Modifier.height(8.dp))
        Column {
            Row {
                repeat(post.score ?: 0) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = StarYellow
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = post.description,
                modifier = Modifier.wrapContentHeight()
            )
        }
    }
}

@Composable
private fun PostHead(
    modifier: Modifier = Modifier,
    post: PostModel,
    onDeleteRequest: (() -> Unit)? = null,
    avatarPlaceholder: Painter? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AuthorAvatar(
            url = post.authorAvatarUrl,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Purple80),
            avatarPlaceholder = avatarPlaceholder
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(post.authorName)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    contentDescription = null,
                    imageVector = Icons.Default.LocationOn,
                    tint = LightGray
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = post.address,
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        onDeleteRequest?.let {
            IconButton(it) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = BasicIconsTint,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun AuthorAvatar(
    modifier: Modifier = Modifier,
    url: String? = null,
    avatarPlaceholder: Painter? = null
) {
    if (url != null) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop,
            placeholder = avatarPlaceholder
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Person, null)
        }
    }
}

@Preview
@Composable
fun PostItem_Preview() {
    val post = remember {
        PostModel(
            id = "SomeUserId",
            authorName = "Ivan Ivanov",
            authorAvatarUrl = "https://img.magnific.com/free-photo/handsome-young-cheerful-man-with-arms-crossed_171337-1073.jpg?semt=ais_hybrid&w=740&q=80",
            address = "Университетский проспект, 23к3",
            imageUrl = "https://avatarko.ru/img/kartinka/1/multfilm_pingviny.jpg",
            description = "A funny picture. I made it when I was bored with my homework. Tomorrow I will make a very interesting post with a fancy picture and a story. Be ready for it.",
            score = 4
        )
    }
    PostItem(
        post = post,
        modifier = Modifier,
        onDeleteRequest = {},
        placeholder = painterResource(R.drawable.image_placeholder),
        avatarPlaceholder = painterResource(R.drawable.someone)
    )
}
