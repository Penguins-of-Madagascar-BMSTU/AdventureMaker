package com.softcat.adventuremaker.screens.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.domain.entities.Place
import com.softcat.adventuremaker.R
import com.softcat.adventuremaker.screens.search.SearchCategoryModel
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import com.softcat.adventuremaker.ui.theme.BasicIconsTint
import com.softcat.adventuremaker.ui.theme.BasicOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryChipsSection(
    categories: List<SearchCategoryModel>,
    onCategorySelected: (Place.Category) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categories.forEach { category ->
            CategoryFilterChip(
                category = category,
                onClick = { onCategorySelected(category.key) },
                modifier = Modifier
            )
        }
    }
}

@Composable
fun CategoryFilterChip(
    category: SearchCategoryModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(percent = 50)
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = shape,
        color = if (category.isSelected) BasicOrange else White,
        border = BorderStroke(1.dp, BasicOrange)
    ) {
        Text(
            modifier = Modifier.padding(PaddingValues(horizontal = 16.dp, vertical = 10.dp)),
            text = stringResource(category.titleResId),
            style = MaterialTheme.typography.labelLarge,
            color = if (category.isSelected) White else BasicIconsTint
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryFilterChipPreview() {
    AdventureMakerTheme {
        CategoryFilterChip(
            onClick = {},
            category = SearchCategoryModel(
                key = Place.Category.Unknown,
                titleResId = R.string.search_sheet_category_all,
                isSelected = true,
            )
        )
    }
}
