package com.softcat.adventuremaker.screens.posts

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.BasicOrange
import com.softcat.adventuremaker.ui.theme.LightGray
import com.softcat.adventuremaker.ui.theme.StarYellow
import org.koin.androidx.compose.koinViewModel
import androidx.compose.ui.unit.dp

@Composable
fun CreatePostContent(
    navController: NavController,
    viewModel: CreatePostViewModel = koinViewModel()
) {
    val state by viewModel.state.observeAsState(CreatePostState.Editing())
    val editingState = state as? CreatePostState.Editing ?: CreatePostState.Editing()
    CreatePostLayout(
        navController = navController,
        editingState = editingState,
        onDescriptionChange = viewModel::updateDescription,
        onBackClick = { navController.popBackStack() }
    )
}

@Composable
private fun CreatePostLayout(
    navController: NavController,
    editingState: CreatePostState.Editing,
    onDescriptionChange: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.Networking,
                navController = navController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(R.drawable.chevron_backward),
                        contentDescription = null,
                        tint = BasicIconsTint
                    )
                }
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.navigation_networking_label),
                    style = MaterialTheme.typography.headlineSmall,
                    color = Black,
                    textAlign = TextAlign.Center
                )
                Icon(
                    modifier = Modifier.padding(start = 16.dp).size(18.dp),
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = BasicIconsTint
                )
            }

            Text(
                text = stringResource(R.string.create_post_title),
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                value = editingState.description,
                onValueChange = onDescriptionChange,
                placeholder = {
                    Text(stringResource(R.string.create_post_description_placeholder))
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(5) { index ->
                    val selected = (editingState.score ?: 0) > index
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .size(28.dp),
                        imageVector = Icons.Default.Star,
                        tint = if (selected) StarYellow else LightGray,
                        contentDescription = null
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    tint = BasicIconsTint,
                    contentDescription = null
                )
                Icon(
                    modifier = Modifier.padding(start = 12.dp),
                    imageVector = Icons.Default.LocationOn,
                    tint = BasicIconsTint,
                    contentDescription = null
                )
                Button(
                    modifier = Modifier
                        .padding(start = 24.dp)
                        .height(52.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = BasicOrange),
                    shape = RoundedCornerShape(28.dp),
                    onClick = {}
                ) {
                    Text(
                        text = stringResource(R.string.create_post_publish),
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CreatePostContentPreview() {
    CreatePostLayout(
        navController = rememberNavController(),
        editingState = CreatePostState.Editing(
            description = "",
            score = 3
        ),
        onDescriptionChange = {},
        onBackClick = {}
    )
}

