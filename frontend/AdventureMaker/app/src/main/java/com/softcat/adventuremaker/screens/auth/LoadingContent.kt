package com.softcat.adventuremaker.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.ui.theme.BasicOrange

@Composable
fun LoadingContent() {
    Scaffold(
        topBar = {
            AuthTopBar(
                onBackClick = {},
                titleResId = R.string.loading
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(64.dp),
                color = BasicOrange
            )
        }
    }
}