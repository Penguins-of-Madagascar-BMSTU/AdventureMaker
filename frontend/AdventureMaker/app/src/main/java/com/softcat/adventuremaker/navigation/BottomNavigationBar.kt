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
import androidx.compose.ui.unit.dp
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.BasicOrange
import com.softcat.adventuremaker.navigation.NavigationItem.BottomBarConfiguration
import com.softcat.adventuremaker.ui.theme.NavBarShadow

@Composable
fun BottomNavigationBarElement(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    iconResId: Int,
    labelResId: Int,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        IconButton(
            modifier = Modifier
                .size(56.dp, 32.dp),
            onClick = onClick,
            colors = IconButtonDefaults.iconButtonColors().copy(
                containerColor = if (isActive) BasicOrange else White
            )
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(iconResId),
                tint = BasicIconsTint,
                contentDescription = null
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            text = stringResource(labelResId),
            style = MaterialTheme.typography.bodySmall,
            color = Black
        )
    }
}

@Composable
fun BottomNavigationBar(
    configuration: BottomBarConfiguration,
    onTabSelected: (BottomBarConfiguration) -> Unit
) {
    val shadow = Brush.linearGradient(
        colors = listOf(NavBarShadow, White),
        start = Offset(0f, Float.POSITIVE_INFINITY),
        end = Offset(0f, 0f)
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
            colors = CardDefaults.cardColors(containerColor = White),
            shape = RectangleShape
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomNavigationBarElement(
                    modifier = Modifier.wrapContentHeight().weight(1f),
                    isActive = configuration == BottomBarConfiguration.Tools,
                    iconResId = R.drawable.tools,
                    labelResId = R.string.navigation_tools_label,
                    onClick = { onTabSelected(BottomBarConfiguration.Tools) }
                )
                BottomNavigationBarElement(
                    modifier = Modifier.wrapContentHeight().weight(1f),
                    isActive = configuration == BottomBarConfiguration.Search,
                    iconResId = R.drawable.search,
                    labelResId = R.string.navigation_search_label,
                    onClick = { onTabSelected(BottomBarConfiguration.Search) }
                )
                BottomNavigationBarElement(
                    modifier = Modifier.wrapContentHeight().weight(1f),
                    isActive = configuration == BottomBarConfiguration.Networking,
                    iconResId = R.drawable.networking,
                    labelResId = R.string.navigation_networking_label,
                    onClick = { onTabSelected(BottomBarConfiguration.Networking) }
                )
                BottomNavigationBarElement(
                    modifier = Modifier.wrapContentHeight().weight(1f),
                    isActive = configuration == BottomBarConfiguration.Favourites,
                    iconResId = R.drawable.heart,
                    labelResId = R.string.navigation_favourites_label,
                    onClick = { onTabSelected(BottomBarConfiguration.Favourites) }
                )
            }
        }
    }
}