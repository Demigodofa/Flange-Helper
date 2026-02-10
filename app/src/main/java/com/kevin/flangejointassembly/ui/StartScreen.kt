package com.kevin.flangejointassembly.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import com.kevin.flangejointassembly.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.LinearProgressIndicator
import kotlin.math.roundToInt
import java.util.Locale

@Composable
fun StartScreen(
    jobs: List<JobItem>,
    storageUsedBytes: Long,
    storageLimitBytes: Long,
    onCreateJob: () -> Unit,
    onJobClick: (JobItem) -> Unit,
    onEditJob: (JobItem) -> Unit,
    onExportJob: (JobItem, PdfExportMode) -> Unit,
    onDeleteJob: (JobItem) -> Unit
) {
    val jobToDelete = remember { mutableStateOf<JobItem?>(null) }
    val jobToExport = remember { mutableStateOf<JobItem?>(null) }

    Scaffold(
        containerColor = FlangeColors.ScreenBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(FlangeColors.ScreenBackground)
        ) {
            val screenHeight = LocalConfiguration.current.screenHeightDp.dp
            val logoSize = (screenHeight * 0.25f).coerceIn(96.dp, 160.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.flange_helper_512),
                    contentDescription = "Flange Helper Logo",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(logoSize)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onCreateJob,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.75f)
                        .height(54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlangeColors.PrimaryButton,
                        contentColor = FlangeColors.PrimaryButtonText
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "Create Job",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(
                    color = FlangeColors.Divider,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                StorageMeter(
                    usedBytes = storageUsedBytes,
                    limitBytes = storageLimitBytes
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (jobs.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No jobs yet. Create your first job above.",
                            textAlign = TextAlign.Center,
                            color = FlangeColors.TextMuted
                        )
                    }
                } else {
                    Text(
                        text = "Current Jobs",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = FlangeColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(jobs, key = { it.id }) { job ->
                            JobCard(
                                job = job,
                                onClick = { onJobClick(job) },
                                onEdit = { onEditJob(job) },
                                onExport = { jobToExport.value = job },
                                onDelete = { jobToDelete.value = job }
                            )
                        }
                    }
                }
            }
        }
    }

    val pendingDelete = jobToDelete.value
    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { jobToDelete.value = null },
            title = { Text("Delete Job") },
            text = { Text("Are you sure you want to delete job ${pendingDelete.number}?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteJob(pendingDelete)
                    jobToDelete.value = null
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { jobToDelete.value = null }) {
                    Text("No")
                }
            }
        )
    }

    val pendingExport = jobToExport.value
    if (pendingExport != null) {
        AlertDialog(
            onDismissRequest = { jobToExport.value = null },
            title = { Text("Export Job") },
            text = { Text("Choose export size:") },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        onExportJob(pendingExport, PdfExportMode.EMAIL_FRIENDLY)
                        jobToExport.value = null
                    }) {
                        Text("Email-friendly")
                    }
                    TextButton(onClick = {
                        onExportJob(pendingExport, PdfExportMode.FULL_RES)
                        jobToExport.value = null
                    }) {
                        Text("Full-res")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { jobToExport.value = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun StorageMeter(
    usedBytes: Long,
    limitBytes: Long
) {
    val safeLimit = if (limitBytes > 0) limitBytes else 1L
    val ratio = (usedBytes.toFloat() / safeLimit.toFloat()).coerceIn(0f, 1f)
    val percent = (ratio * 100).roundToInt()
    val usedMb = usedBytes / (1024f * 1024f)
    val limitMb = limitBytes / (1024f * 1024f)

    Column {
        Text(
            text = "Storage Usage: $percent%",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { ratio },
            color = FlangeColors.PrimaryButton,
            trackColor = FlangeColors.Divider,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = String.format(Locale.US, "%.1f MB of %.0f MB used", usedMb, limitMb),
            style = TextStyle(
                fontSize = 12.sp
            ),
            color = FlangeColors.TextSecondary
        )
    }
}

@Composable
private fun JobCard(
    job: JobItem,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = FlangeColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onClick),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Job# ${job.number}",
                        color = FlangeColors.PrimaryButton,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = job.location.ifBlank { "-" },
                        color = FlangeColors.TextSecondary,
                        textDecoration = TextDecoration.Underline
                    )
                    Text(
                        text = formatDate(job.dateMillis),
                        color = FlangeColors.TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlangeColors.EditButton,
                        contentColor = FlangeColors.EditButtonText
                    )
                ) {
                    Text("Edit Job")
                }
                Button(
                    onClick = onExport,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlangeColors.ExportButton,
                        contentColor = FlangeColors.ExportButtonText
                    )
                ) {
                    Text("Export Job")
                }
                Button(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FlangeColors.DeleteButton,
                        contentColor = FlangeColors.DeleteButtonText
                    )
                ) {
                    Text("Delete Job")
                }
            }
        }
    }
}
