package com.softcat.adventuremaker.screens.auth

import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.ui.theme.BasicIconsTint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTopBar(
    titleResId: Int,
    onBackClick: () -> Unit,
    isBackButtonVisible: Boolean = false
) {
    TopAppBar(
        expandedHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight,
        windowInsets = TopAppBarDefaults.windowInsets.only(WindowInsetsSides.Horizontal),
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(titleResId),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = Black
            )
        },
        navigationIcon = {
            if (isBackButtonVisible) {
                IconButton(onBackClick) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(R.drawable.chevron_backward),
                        contentDescription = null,
                        tint = BasicIconsTint
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = White
        )
    )
}