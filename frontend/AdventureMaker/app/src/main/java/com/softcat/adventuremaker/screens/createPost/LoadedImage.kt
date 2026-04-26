package com.softcat.adventuremaker.screens.createPost

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.softcat.adventuremaker.ui.theme.LightGray

@Composable
fun LoadedImage(
    modifier: Modifier = Modifier,
    uri: Uri? = null,
) {
    if (uri == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(128.dp),
                contentDescription = null,
                imageVector = Icons.Default.Image,
                tint = LightGray
            )
        }
    } else {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .crossfade(true)
                .build(),
            contentDescription = "Выбранное изображение",
            modifier = modifier
        )
    }
}