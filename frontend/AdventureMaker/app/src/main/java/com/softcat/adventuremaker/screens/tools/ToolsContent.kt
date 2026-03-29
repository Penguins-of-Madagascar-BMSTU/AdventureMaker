package com.softcat.adventuremaker.screens.tools

import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.navigation.BottomNavigationBar
import com.softcat.adventuremaker.navigation.NavigationItem
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import com.softcat.adventuremaker.ui.theme.BasicOrange
import com.softcat.adventuremaker.ui.theme.LightGray
import org.koin.androidx.compose.koinViewModel

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

private fun flagEmoji(code: String): String = when (code) {
    "RUB" -> "🇷🇺"
    "USD" -> "🇺🇸"
    "EUR" -> "🇪🇺"
    "CNY" -> "🇨🇳"
    "BYN" -> "🇧🇾"
    else -> "?"
}

@Composable
private fun CurrencyConverterTable(
    amounts: Map<String, String>,
    onValueChange: (String, String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightGray, RoundedCornerShape(8.dp)),
    ) {
            ToolsCurrencies.codes.forEachIndexed { index, code ->
                if (index > 0) {
                    HorizontalDivider(color = LightGray, thickness = 1.dp)
                }
            CurrencyConverterRow(
                code = code,
                value = amounts[code].orEmpty(),
                flagEmoji = flagEmoji(code),
                onValueChange = { onValueChange(code, it) },
            )
        }
    }
}

@Composable
private fun CurrencyConverterRow(
    code: String,
    value: String,
    flagEmoji: String,
    onValueChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = code,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Black,
            modifier = Modifier.width(44.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.End,
                color = Black,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = White,
                unfocusedContainerColor = White,
                disabledContainerColor = White,
                focusedBorderColor = LightGray,
                unfocusedBorderColor = LightGray,
                cursorColor = BasicOrange,
            ),
        )
        Text(
            text = flagEmoji,
            fontSize = 22.sp,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ToolsSectionPlaceholder(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = LightGray,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ToolsContent(
    navController: NavController,
    viewModel: ToolsViewModel = koinViewModel(),
) {
    var selected by remember { mutableStateOf(ToolsCategoryStub.Currency) }
    val state by viewModel.state.observeAsState(ToolsState.CurrencyConverter())

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
                        onClick = {
                            selected = ToolsCategoryStub.Currency
                            viewModel.openCurrencyConverter()
                        },
                    )
                    ToolChipStub(
                        label = stringResource(R.string.tools_chip_translation),
                        selected = selected == ToolsCategoryStub.Translation,
                        onClick = {
                            selected = ToolsCategoryStub.Translation
                            viewModel.openTranslation()
                        },
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
                        onClick = {
                            selected = ToolsCategoryStub.Emergency
                            viewModel.openEmergencyNumbers()
                        },
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                when (selected) {
                    ToolsCategoryStub.Currency -> {
                        val conv = state as? ToolsState.CurrencyConverter
                        if (conv != null) {
                            Column(Modifier.padding(top = 28.dp)) {
                                CurrencyConverterTable(
                                    amounts = conv.amounts,
                                    onValueChange = viewModel::updateCurrencyInput,
                                )
                            }
                        }
                    }

                    ToolsCategoryStub.Translation -> {
                        ToolsSectionPlaceholder("soon")
                    }

                    ToolsCategoryStub.Phrases,
                    ToolsCategoryStub.Cultural,
                    -> {
                        ToolsSectionPlaceholder("soon")
                    }

                    ToolsCategoryStub.Emergency -> {
                        when (val s = state) {
                            is ToolsState.EmergencyNumbers -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .padding(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    s.numbers.forEach { (title, value) ->
                                        Text(
                                            text = "$title — $value",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Black,
                                        )
                                    }
                                }
                            }

                            else -> {
                                ToolsSectionPlaceholder("soon")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyConverterTablePreview() {
    AdventureMakerTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp, bottom = 16.dp),
        ) {
            CurrencyConverterTable(
                amounts = mapOf(
                    "RUB" to "7727.34",
                    "USD" to "100",
                    "EUR" to "84.3492",
                    "CNY" to "687.232",
                    "BYN" to "285.424",
                ),
                onValueChange = { _, _ -> },
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
