package com.kevin.flangejointassembly.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kevin.flangejointassembly.ui.components.FlangeHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobFormScreen(
    initialJobNumber: String,
    initialLocation: String,
    initialDateMillis: Long?,
    isEditing: Boolean,
    onSave: (String, String, Long) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var jobNumber by remember { mutableStateOf(initialJobNumber) }
    var location by remember { mutableStateOf(initialLocation) }
    var dateMillis by remember { mutableLongStateOf(initialDateMillis ?: todayMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = datePickerState.selectedDateMillis
                    if (selected != null) {
                        dateMillis = normalizePickerMillis(selected)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FlangeColors.ScreenBackground)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        FlangeHeader(onBack = onBack)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (isEditing) "Edit Job" else "Create Job",
            style = MaterialTheme.typography.titleLarge,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Job Number",
            style = MaterialTheme.typography.labelLarge,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = jobNumber,
            onValueChange = { input ->
                jobNumber = input.uppercase().take(10)
            },
            singleLine = true,
            placeholder = { Text("Up to 10 characters") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Job Location",
            style = MaterialTheme.typography.labelLarge,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            minLines = 2,
            maxLines = 2,
            placeholder = { Text("Job location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Date",
            style = MaterialTheme.typography.labelLarge,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = formatDate(dateMillis),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            trailingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Select date",
                    modifier = Modifier.clickable { showDatePicker = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSave(jobNumber.trim(), location.trim(), dateMillis) },
            enabled = jobNumber.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FlangeColors.PrimaryButton,
                contentColor = FlangeColors.PrimaryButtonText
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 6.dp,
                pressedElevation = 2.dp
            )
        ) {
            Text(
                text = if (isEditing) "Save Job" else "Create Job",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}
