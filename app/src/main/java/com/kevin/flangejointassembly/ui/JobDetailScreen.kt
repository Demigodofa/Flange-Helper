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
fun JobDetailScreen(
    jobNumber: String,
    onBack: () -> Unit
) {
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
                text = "Job $jobNumber",
                style = MaterialTheme.typography.titleLarge,
                color = FlangeColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Flange tightening details will be built here next.",
                style = MaterialTheme.typography.bodyMedium,
                color = FlangeColors.TextSecondary
            )
        }
    }
}
