package com.kevin.flangejointassembly.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevin.flangejointassembly.ui.components.FlangeHeader

@Composable
fun FormScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    Scaffold(
        containerColor = FlangeColors.ScreenBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(FlangeColors.ScreenBackground)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            FlangeHeader(onBack = onBack)
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Flange Report",
                style = MaterialTheme.typography.titleLarge,
                color = FlangeColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "First input screen placeholder. Next you’ll define the fields.",
                style = MaterialTheme.typography.bodyMedium,
                color = FlangeColors.TextSecondary
            )
        }
    }
}
