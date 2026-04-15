package com.softcat.adventuremaker.screens.details

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.CustomRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun DetailsAppBar(
    changeFavouriteStatus: () -> Unit = {},
    onBackClicked: () -> Unit = {},
    isFavourite: Boolean? = true
) {
    TopAppBar(
        expandedHeight = TopAppBarDefaults.MediumAppBarCollapsedHeight,
        windowInsets = TopAppBarDefaults.windowInsets,
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.app_bar_title_details),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                color = Black
            )
        },
        actions = {
            isFavourite?.let {
                IconButton(
                    onClick = changeFavouriteStatus
                ) {
                    Icon(
                        modifier = Modifier.size(32.dp),
                        painter = painterResource(if (isFavourite) R.drawable.heart_filled else R.drawable.heart),
                        tint = CustomRed,
                        contentDescription = null
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onBackClicked) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.chevron_backward),
                    contentDescription = null,
                    tint = BasicIconsTint
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = White
        )
    )
}