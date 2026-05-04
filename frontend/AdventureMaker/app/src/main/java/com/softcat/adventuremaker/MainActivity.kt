package com.softcat.adventuremaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.softcat.adventuremaker.navigation.MainScreen
import com.softcat.adventuremaker.ui.theme.AdventureMakerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdventureMakerTheme {
                MainScreen()
            }
        }
    }
}