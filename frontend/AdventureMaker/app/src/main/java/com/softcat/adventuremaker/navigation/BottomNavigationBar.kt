package com.softcat.adventuremaker.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.ui.theme.BasicOrange
import com.softcat.adventuremaker.ui.theme.LightGray
import com.softcat.adventuremaker.ui.theme.NavigationBarTint

private fun iconResId(selection: MainNavigation) = when (selection) {
    MainNavigation.Favourites -> R.drawable.heart
    MainNavigation.Networking -> R.drawable.networking
    MainNavigation.Search -> R.drawable.search
    MainNavigation.Utils -> R.drawable.utils
}

private fun labelResId(selection: MainNavigation) = when (selection) {
    MainNavigation.Favourites -> R.string.navigation_favourites_label
    MainNavigation.Networking -> R.string.navigation_networking_label
    MainNavigation.Search -> R.string.navigation_search_label
    MainNavigation.Utils -> R.string.navigation_utils_label
}

@Composable
@Preview
fun BottomNavigationBarElement(
    modifier: Modifier = Modifier,
    selection: MainNavigation = MainNavigation.Search,
    onSelected: (MainNavigation) -> Unit = {},
    isActive: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier = Modifier
                .size(56.dp, 32.dp),
            onClick = { onSelected(selection) },
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = if (isActive) BasicOrange else White
            )
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(iconResId(selection)),
                tint = NavigationBarTint,
                contentDescription = null
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(labelResId(selection)),
            style = MaterialTheme.typography.bodySmall,
            color = Black
        )
    }
}

@Composable
@Preview(showBackground = true)
fun BottomNavigationBar(
    selection: MainNavigation = MainNavigation.Search,
    onSelectionChanged: (MainNavigation) -> Unit = {}
) {
    val shadow = Brush.linearGradient(
        colors = listOf(LightGray, White),
        start = Offset(0f, Float.POSITIVE_INFINITY),
        end = Offset(0f, 0f)
    )
    val selections = listOf(
        MainNavigation.Utils to R.drawable.utils,
        MainNavigation.Search to R.drawable.search,
        MainNavigation.Networking to R.drawable.networking,
        MainNavigation.Favourites to R.drawable.heart
    )
    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(shadow)
            .offset(y = 8.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxSize(),
            colors = CardDefaults.cardColors(
                containerColor = White
            ),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                selections.forEach { element ->
                    BottomNavigationBarElement(
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f),
                        selection = element.first,
                        isActive = element.first == selection,
                        onSelected = onSelectionChanged
                    )
                }
            }
        }
    }
}