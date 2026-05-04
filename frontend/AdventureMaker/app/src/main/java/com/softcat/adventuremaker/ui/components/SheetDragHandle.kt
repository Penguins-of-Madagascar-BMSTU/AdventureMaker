package com.softcat.adventuremaker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme
import com.softcat.adventuremaker.ui.theme.BasicIconsTint

@Composable
fun SheetDragHandle(
    modifier: Modifier = Modifier,
    color: Color = BasicIconsTint.copy(alpha = 0.35f)
) {
    Box(
        modifier = modifier
            .size(width = 44.dp, height = 5.dp)
            .background(color = color, shape = CircleShape)
    )
}

@Preview(showBackground = true)
@Composable
private fun SheetDragHandlePreview() {
    AdventureMakerTheme {
        SheetDragHandle()
    }
}
