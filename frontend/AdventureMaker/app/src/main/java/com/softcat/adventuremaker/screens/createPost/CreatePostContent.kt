package com.softcat.adventuremaker.screens.createPost

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.BasicOrange
import com.softcat.adventuremaker.ui.theme.LightGray
import com.softcat.adventuremaker.ui.theme.StarYellow
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import org.koin.core.parameter.parametersOf

@Composable
fun CreatePostContent(
    navController: NavController,
    userId: String,
) {
    val viewModel: CreatePostViewModel = koinViewModel { parametersOf(userId) }
    val state by viewModel.state.observeAsState(CreatePostState())
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let { viewModel.updateImage(it) }
        }
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    )

    val onPhotoSelectClick: () -> Unit = {
        val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }

    CreatePostLayout(
        navController = navController,
        state = state,
        onDescriptionChange = viewModel::updateDescription,
        onPublishClick = viewModel::publishPost,
        onScoreChanged = viewModel::updateScore,
        onSelectPhotoClick = onPhotoSelectClick
    )

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                CreatePostEvent.Published -> { navController.popBackStack() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatePostLayout(
    navController: NavController,
    state: CreatePostState,
    onDescriptionChange: (String) -> Unit,
    onPublishClick: () -> Unit,
    onScoreChanged: (Int?) -> Unit,
    onSelectPhotoClick: () -> Unit
) {
    Scaffold(
        topBar = { CreatePostTopBar(onBackClick = { navController.popBackStack() }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                LoadedImage(
                    uri = state.imageUri,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    placeholder = {
                        Text(stringResource(R.string.create_post_description_placeholder))
                    }
                )
                ScoreSelector(
                    onScoreChanged = onScoreChanged,
                    score = state.score
                )
                ButtonRow(
                    onSelectPhotoClick = onSelectPhotoClick,
                    onPublishClick = onPublishClick
                )
            }
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = BasicOrange
                )
            }
        }
    }
}

@Composable
private fun ScoreSelector(
    onScoreChanged: (Int?) -> Unit,
    score: Int?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onScoreChanged(null) },
            modifier = Modifier.size(32.dp).padding(horizontal = 4.dp)
        ) {
            Icon(
                modifier = Modifier.fillMaxSize(),
                imageVector = Icons.Default.Delete,
                tint = LightGray,
                contentDescription = null
            )
        }
        repeat(5) { index ->
            val selected = (score ?: 0) > index
            IconButton(
                onClick = { onScoreChanged(index + 1) },
                modifier = Modifier.size(48.dp).padding(horizontal = 4.dp)
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = Icons.Default.Star,
                    tint = if (selected) StarYellow else LightGray,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun ButtonRow(
    onSelectPhotoClick: () -> Unit,
    onPublishClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onSelectPhotoClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AttachFile,
                tint = BasicIconsTint,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
        Button(
            modifier = Modifier
                .padding(start = 24.dp)
                .wrapContentHeight()
                .weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = BasicOrange),
            shape = RoundedCornerShape(28.dp),
            onClick = onPublishClick
        ) {
            Text(
                text = stringResource(R.string.create_post_publish),
                color = White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreatePostContentPreview() {
    AdventureMakerTheme {
        CreatePostLayout(
            navController = rememberNavController(),
            state = CreatePostState(
                description = "",
                score = 3
            ),
            onDescriptionChange = {},
            onPublishClick = {},
            onScoreChanged = {},
            onSelectPhotoClick = {},
        )
    }
}

