package com.kevin.flangejointassembly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kevin.flangejointassembly.ui.theme.FlangeJointAssemblyHelperTheme
import com.kevin.flangejointassembly.ui.FormScreen
import com.kevin.flangejointassembly.ui.SplashScreen
import com.kevin.flangejointassembly.ui.StartScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlangeJointAssemblyHelperTheme {
                FlangeApp()
            }
        }
    }
}

@Composable
fun FlangeApp() {
    var showSplash by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf(FlangeScreen.Start) }

    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
    } else {
        when (currentScreen) {
            FlangeScreen.Start -> StartScreen(
                onStart = { currentScreen = FlangeScreen.Form }
            )
            FlangeScreen.Form -> FormScreen(
                onBack = { currentScreen = FlangeScreen.Start }
            )
        }
    }
}

enum class FlangeScreen {
    Start,
    Form
}
