package com.softcat.adventuremaker.screens.tools

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import com.softcat.adventuremaker.ui.theme.BasicOrange

private enum class ToolsCategoryStub {
    Currency,
    Translation,
    Phrases,
    Cultural,
    Emergency,
}

@Composable
private fun ToolChipStub(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = if (selected) BasicOrange else White,
        border = if (!selected) BorderStroke(1.dp, BasicOrange) else null,
        onClick = onClick,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 19.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = if (selected) White else Black,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ToolsContent(
    navController: NavController,
) {
    var selected by remember { mutableStateOf(ToolsCategoryStub.Currency) }

    Scaffold(
        containerColor = White,
        bottomBar = {
            BottomNavigationBar(
                configuration = NavigationItem.BottomBarConfiguration.Tools,
                navController = navController,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(White)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.tools_screen_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ToolChipStub(
                        label = stringResource(R.string.tools_chip_currency),
                        selected = selected == ToolsCategoryStub.Currency,
                        onClick = { selected = ToolsCategoryStub.Currency },
                    )
                    ToolChipStub(
                        label = stringResource(R.string.tools_chip_translation),
                        selected = selected == ToolsCategoryStub.Translation,
                        onClick = { selected = ToolsCategoryStub.Translation },
                    )
                    ToolChipStub(
                        label = stringResource(R.string.tools_chip_phrases),
                        selected = selected == ToolsCategoryStub.Phrases,
                        onClick = { selected = ToolsCategoryStub.Phrases },
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ToolChipStub(
                        label = stringResource(R.string.tools_chip_cultural),
                        selected = selected == ToolsCategoryStub.Cultural,
                        onClick = { selected = ToolsCategoryStub.Cultural },
                    )
                    ToolChipStub(
                        label = stringResource(R.string.tools_chip_emergency),
                        selected = selected == ToolsCategoryStub.Emergency,
                        onClick = { selected = ToolsCategoryStub.Emergency },
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ToolsContentPreview() {
    AdventureMakerTheme {
        ToolsContent(navController = rememberNavController())
    }
}
