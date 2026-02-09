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
    job: JobItem,
    onNewFlangeForm: () -> Unit,
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
                text = "Job ${job.number}",
                style = MaterialTheme.typography.titleLarge,
                color = FlangeColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = job.location.ifBlank { "Job location" },
                style = MaterialTheme.typography.bodyMedium,
                color = FlangeColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatDate(job.dateMillis),
                style = MaterialTheme.typography.bodySmall,
                color = FlangeColors.TextMuted
            )
            Spacer(modifier = Modifier.height(20.dp))
            androidx.compose.material3.Button(
                onClick = onNewFlangeForm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = FlangeColors.ExportButton,
                    contentColor = FlangeColors.ExportButtonText
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp)
            ) {
                Text("New Flange Bolting Form")
            }
        }
    }
}
