package com.softcat.adventuremaker.screens.tools

import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.BasicOrange
import com.softcat.adventuremaker.ui.theme.EmergencyTableLabelBackground
import com.softcat.adventuremaker.ui.theme.LightGray
import com.softcat.adventuremaker.ui.theme.TranslationActionButton
import org.koin.androidx.compose.koinViewModel

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
private fun EmergencyNumbersTable(
    rows: List<Pair<String, String>>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightGray, RoundedCornerShape(8.dp)),
    ) {
        rows.forEachIndexed { index, (number, description) ->
            if (index > 0) {
                HorizontalDivider(color = LightGray, thickness = 1.dp)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .heightIn(min = 52.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = description,
                    modifier = Modifier
                        .weight(1.15f)
                        .background(EmergencyTableLabelBackground)
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Black,
                )
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    thickness = 1.dp,
                    color = LightGray,
                )
                Text(
                    text = number,
                    modifier = Modifier
                        .weight(1f)
                        .background(White)
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Normal,
                    color = Black,
                )
            }
        }
    }
}

@Composable
private fun languageDisplayName(languageCode: String): String =
    when (languageCode.lowercase()) {
        "en" -> stringResource(R.string.lang_english)
        "ru" -> stringResource(R.string.lang_russian)
        else -> languageCode
    }

@Composable
private fun TranslationPanel(
    state: TranslationState,
    translationInProgress: Boolean,
    onSourceChange: (String) -> Unit,
    onSwap: () -> Unit,
    onTranslate: () -> Unit,
) {
    val hint = stringResource(R.string.tools_translation_hint)
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = languageDisplayName(state.sourceLanguage),
                style = MaterialTheme.typography.bodyLarge,
                color = Black,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = onSwap,
                enabled = !translationInProgress,
            ) {
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = null,
                    tint = BasicIconsTint,
                )
            }
            Text(
                text = languageDisplayName(state.targetLanguage),
                style = MaterialTheme.typography.bodyLarge,
                color = Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
            )
        }
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LightGray, RoundedCornerShape(8.dp)),
        ) {
            OutlinedTextField(
                value = state.sourceText,
                onValueChange = onSourceChange,
                enabled = !translationInProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = {
                    Text(
                        text = hint,
                        color = LightGray,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = BasicOrange,
                ),
                shape = RoundedCornerShape(
                    topStart = 8.dp,
                    topEnd = 8.dp,
                    bottomStart = 0.dp,
                    bottomEnd = 0.dp,
                ),
                maxLines = 8,
            )
            HorizontalDivider(color = LightGray, thickness = 1.dp)
            OutlinedTextField(
                value = if (state.translatedText == "Translation failed") {
                        stringResource(R.string.translate_error)
                    } else {
                        state.translatedText
                    },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                placeholder = {
                    Text(
                        text = hint,
                        color = LightGray,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    disabledContainerColor = White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedTextColor = Black,
                    unfocusedTextColor = Black,
                    disabledTextColor = Black,
                    cursorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 8.dp,
                    bottomEnd = 8.dp,
                ),
                maxLines = 8,
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onTranslate,
            enabled = !translationInProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TranslationActionButton,
                contentColor = White,
            ),
        ) {
            if (translationInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = White,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(R.string.tools_translate_button),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
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
    val state by viewModel.state.observeAsState(ToolsState())
    val scrollState = rememberScrollState()

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
                .verticalScroll(scrollState)
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
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ToolChipStub(
                    label = stringResource(R.string.tools_chip_currency),
                    selected = state.activeSection == ToolsSection.Currency,
                    onClick = { viewModel.openCurrencyConverter() },
                )
                ToolChipStub(
                    label = stringResource(R.string.tools_chip_translation),
                    selected = state.activeSection == ToolsSection.Translation,
                    onClick = { viewModel.openTranslation() },
                )
                ToolChipStub(
                    label = stringResource(R.string.tools_chip_emergency),
                    selected = state.activeSection == ToolsSection.Emergency,
                    onClick = { viewModel.openEmergencyNumbers() },
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                when (state.activeSection) {
                    ToolsSection.Currency -> {
                        Column(Modifier.padding(top = 28.dp)) {
                            CurrencyConverterTable(
                                amounts = state.currencyAmounts,
                                onValueChange = viewModel::updateCurrencyInput,
                            )
                            if (state.currencyConversionInProgress) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .size(32.dp),
                                    color = BasicOrange,
                                    strokeWidth = 3.dp,
                                )
                            }
                        }
                    }

                    ToolsSection.Translation -> {
                        Column(Modifier.padding(top = 28.dp)) {
                            TranslationPanel(
                                state = state.translation,
                                translationInProgress = state.translationInProgress,
                                onSourceChange = viewModel::changeTextToTranslate,
                                onSwap = viewModel::swapTranslationLanguages,
                                onTranslate = viewModel::translateText,
                            )
                        }
                    }

                    ToolsSection.Emergency -> {
                        val numbers = state.emergencyNumbers
                        if (numbers != null) {
                            Column(Modifier.padding(top = 28.dp)) {
                                EmergencyNumbersTable(rows = numbers)
                            }
                        } else {
                            ToolsSectionPlaceholder("soon")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TranslationPanelPreview() {
    AdventureMakerTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp, bottom = 16.dp),
        ) {
            TranslationPanel(
                state = TranslationState(
                    sourceText = "Hello",
                    translatedText = "Привет",
                ),
                translationInProgress = false,
                onSourceChange = {},
                onSwap = {},
                onTranslate = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmergencyNumbersTablePreview() {
    AdventureMakerTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 16.dp)
                .padding(top = 28.dp, bottom = 16.dp),
        ) {
            EmergencyNumbersTable(
                rows = listOf(
                    "101" to "пожарная охрана",
                    "102" to "полиция",
                    "103" to "скорая помощь",
                    "104" to "аварийная газовая служба",
                    "112" to "единый номер экстренных служб",
                ),
            )
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
