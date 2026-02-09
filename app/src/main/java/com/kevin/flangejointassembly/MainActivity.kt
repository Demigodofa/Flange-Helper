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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.kevin.flangejointassembly.ui.theme.FlangeJointAssemblyHelperTheme
import com.kevin.flangejointassembly.ui.JobDetailScreen
import com.kevin.flangejointassembly.ui.FlangeFormScreen
import com.kevin.flangejointassembly.ui.JobFormScreen
import com.kevin.flangejointassembly.ui.JobItem
import com.kevin.flangejointassembly.ui.JobStorage
import com.kevin.flangejointassembly.ui.SplashScreen
import com.kevin.flangejointassembly.ui.StartScreen
import java.util.UUID

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
    val context = LocalContext.current
    var showSplash by remember { mutableStateOf(true) }
    var currentScreen by remember { mutableStateOf(FlangeScreen.Start) }
    var jobs by remember { mutableStateOf(listOf<JobItem>()) }
    var selectedJobId by remember { mutableStateOf<String?>(null) }
    var editingJobId by remember { mutableStateOf<String?>(null) }
    var flangeJobId by remember { mutableStateOf<String?>(null) }
    var storageUsedBytes by remember { mutableStateOf(0L) }

    LaunchedEffect(Unit) {
        val loadedJobs = JobStorage.loadJobs(context)
        jobs = loadedJobs
        storageUsedBytes = JobStorage.calculateStorageBytes(context)
    }

    fun persistJobs(updatedJobs: List<JobItem>) {
        jobs = updatedJobs
        JobStorage.saveJobs(context, updatedJobs)
        storageUsedBytes = JobStorage.calculateStorageBytes(context)
    }

    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
    } else {
        when (currentScreen) {
            FlangeScreen.Start -> StartScreen(
                jobs = jobs,
                storageUsedBytes = storageUsedBytes,
                storageLimitBytes = JobStorage.STORAGE_LIMIT_BYTES,
                onCreateJob = {
                    editingJobId = null
                    currentScreen = FlangeScreen.JobForm
                },
                onJobClick = { job ->
                    selectedJobId = job.id
                    currentScreen = FlangeScreen.JobDetail
                },
                onEditJob = { job ->
                    editingJobId = job.id
                    currentScreen = FlangeScreen.JobForm
                },
                onExportJob = { _ ->
                    // TODO: Wire PDF export.
                },
                onDeleteJob = { job ->
                    val updated = jobs.filterNot { it.id == job.id }
                    persistJobs(updated)
                    if (selectedJobId == job.id) {
                        selectedJobId = null
                    }
                    if (editingJobId == job.id) {
                        editingJobId = null
                    }
                }
            )
            FlangeScreen.JobForm -> {
                val editingJob = jobs.find { it.id == editingJobId }
                JobFormScreen(
                    initialJobNumber = editingJob?.number.orEmpty(),
                    initialLocation = editingJob?.location.orEmpty(),
                    initialDateMillis = editingJob?.dateMillis,
                    isEditing = editingJob != null,
                    onSave = { number, location, dateMillis ->
                        if (editingJob == null) {
                            val newJob = JobItem(
                                id = UUID.randomUUID().toString(),
                                number = number,
                                location = location,
                                dateMillis = dateMillis
                            )
                            persistJobs(jobs + newJob)
                            selectedJobId = newJob.id
                        } else {
                            val updatedJobs = jobs.map { job ->
                                if (job.id == editingJob.id) {
                                    job.copy(
                                        number = number,
                                        location = location,
                                        dateMillis = dateMillis
                                    )
                                } else {
                                    job
                                }
                            }
                            persistJobs(updatedJobs)
                        }
                        currentScreen = FlangeScreen.Start
                    },
                    onBack = { currentScreen = FlangeScreen.Start }
                )
            }
            FlangeScreen.JobDetail -> {
                val selectedJob = jobs.find { it.id == selectedJobId }
                if (selectedJob != null) {
                    JobDetailScreen(
                        job = selectedJob,
                        onNewFlangeForm = {
                            flangeJobId = selectedJob.id
                            currentScreen = FlangeScreen.FlangeForm
                        },
                        onBack = { currentScreen = FlangeScreen.Start }
                    )
                } else {
                    currentScreen = FlangeScreen.Start
                }
            }
            FlangeScreen.FlangeForm -> {
                val job = jobs.find { it.id == flangeJobId }
                if (job != null) {
                    FlangeFormScreen(
                        jobId = job.id,
                        jobNumber = job.number,
                        jobDateMillis = job.dateMillis,
                        onSave = { form ->
                            val updated = jobs.map { item ->
                                if (item.id == job.id) {
                                    item.copy(flangeForms = item.flangeForms + form.copy(jobId = job.id))
                                } else {
                                    item
                                }
                            }
                            persistJobs(updated)
                            currentScreen = FlangeScreen.JobDetail
                            selectedJobId = job.id
                        },
                        onBack = { currentScreen = FlangeScreen.JobDetail }
                    )
                } else {
                    currentScreen = FlangeScreen.Start
                }
            }
        }
    }
}

enum class FlangeScreen {
    Start,
    JobForm,
    JobDetail,
    FlangeForm
}
