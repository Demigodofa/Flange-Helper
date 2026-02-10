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
import androidx.compose.material3.TextButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kevin.flangejointassembly.ui.components.FlangeHeader
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row

@Composable
fun JobDetailScreen(
    job: JobItem,
    onNewFlangeForm: () -> Unit,
    onOpenForm: (FlangeFormItem) -> Unit,
    onDeleteForm: (FlangeFormItem) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var pendingDelete by remember { mutableStateOf<FlangeFormItem?>(null) }

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
                    FlangeFormCard(
                        form = form,
                        index = index + 1,
                        onOpen = { onOpenForm(form) },
                        onDelete = { pendingDelete = form }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete Flange Form") },
            text = { Text("Are you sure you want to delete this flange form?") },
            confirmButton = {
                TextButton(onClick = {
                    pendingDelete?.let { onDeleteForm(it) }
                    pendingDelete = null
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
private fun FlangeFormCard(
    form: FlangeFormItem,
    index: Int,
    onOpen: () -> Unit,
    onDelete: () -> Unit
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
            .clickable { onOpen() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = FlangeColors.TextPrimary
            )
            Text(
                text = "Open",
                style = MaterialTheme.typography.labelLarge,
                color = FlangeColors.PrimaryButton
            )
        }
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
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDelete) {
                Text("Delete", color = FlangeColors.DeleteButton)
            }
        }
    }
}
