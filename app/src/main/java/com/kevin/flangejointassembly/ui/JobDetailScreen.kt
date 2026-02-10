package com.kevin.flangejointassembly.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
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
                .verticalScroll(rememberScrollState())
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
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Flange Forms",
                style = MaterialTheme.typography.titleMedium,
                color = FlangeColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (job.flangeForms.isEmpty()) {
                Text(
                    text = "No flange forms yet. Create the first form above.",
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.TextMuted
                )
            } else {
                job.flangeForms.forEachIndexed { index, form ->
                    FlangeFormCard(form = form, index = index + 1)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FlangeFormCard(
    form: FlangeFormItem,
    index: Int
) {
    val title = if (form.description.isNotBlank()) {
        form.description
    } else {
        "Flange Form $index"
    }
    val dateText = formatDate(form.dateMillis)
    val gasketText = form.gasketType.ifBlank { "Gasket: n/a" }
    val boltText = if (form.boltHoles.isNotBlank()) "Bolts: ${form.boltHoles}" else "Bolts: n/a"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FlangeColors.Divider, RoundedCornerShape(14.dp))
            .background(FlangeColors.CardBackground, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodySmall,
            color = FlangeColors.TextMuted
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "$gasketText | $boltText",
            style = MaterialTheme.typography.bodySmall,
            color = FlangeColors.TextSecondary
        )
    }
}
