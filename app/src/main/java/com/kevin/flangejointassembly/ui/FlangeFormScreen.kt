package com.kevin.flangejointassembly.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import com.kevin.flangejointassembly.ui.components.FlangeHeader

private data class DropdownOption(
    val value: String,
    val menuLabel: String = value,
    val displayLabel: String = value
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlangeFormScreen(
    jobId: String,
    jobNumber: String,
    jobDateMillis: Long,
    onSave: (FlangeFormItem) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var dateMillis by remember { mutableLongStateOf(jobDateMillis) }
    var description by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("") }
    var gasketType by remember { mutableStateOf("") }
    var flangeClass by remember { mutableStateOf("") }
    var pipeSize by remember { mutableStateOf("") }
    var customId by remember { mutableStateOf("") }
    var customOd by remember { mutableStateOf("") }
    var customThickness by remember { mutableStateOf("") }
    var flangeFace by remember { mutableStateOf("") }
    var boltHoles by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = it }
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
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        FlangeHeader(onBack = onBack)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "New Flange Bolting Form",
            style = MaterialTheme.typography.titleLarge,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        LabeledField(label = "Job Number") {
            OutlinedTextField(
                value = jobNumber,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        LabeledField(label = "Date") {
            OutlinedTextField(
                value = formatDate(dateMillis),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Select date"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )
        }

        LabeledField(label = "Flange Description / Location") {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                minLines = 3,
                maxLines = 3,
                placeholder = { Text("Describe location and purpose") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        LabeledField(label = "Service Type") {
            OutlinedTextField(
                value = serviceType,
                onValueChange = { serviceType = it.take(20) },
                singleLine = true,
                placeholder = { Text("Up to 20 characters") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        LabeledField(label = "Gasket Type") {
            DropdownField(
                value = gasketType,
                options = gasketTypeOptions(),
                placeholder = "Select gasket type",
                onValueChange = { gasketType = it }
            )
        }

        LabeledField(label = "Flange Class") {
            DropdownField(
                value = flangeClass,
                options = flangeClassOptions(),
                placeholder = "Select flange class",
                onValueChange = { flangeClass = it }
            )
        }

        LabeledField(label = "Pipe Size") {
            OutlinedTextField(
                value = pipeSize,
                onValueChange = { pipeSize = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "If Custom Body Flange",
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = customId,
                onValueChange = { customId = it.take(5) },
                label = { Text("I.D.") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = customOd,
                onValueChange = { customOd = it.take(5) },
                label = { Text("O.D.") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = customThickness,
                onValueChange = { customThickness = it.take(5) },
                label = { Text("Thk") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }

        LabeledField(label = "Flange Face") {
            DropdownField(
                value = flangeFace,
                options = flangeFaceOptions(),
                placeholder = "Select flange face",
                onValueChange = { flangeFace = it }
            )
        }

        LabeledField(label = "Number of Bolt Holes") {
            DropdownField(
                value = boltHoles,
                options = boltHoleOptions(),
                placeholder = "Select bolt holes",
                onValueChange = { boltHoles = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                onSave(
                    FlangeFormItem(
                        id = java.util.UUID.randomUUID().toString(),
                        jobId = jobId,
                        dateMillis = dateMillis,
                        description = description.trim(),
                        serviceType = serviceType.trim(),
                        gasketType = gasketType,
                        flangeClass = flangeClass,
                        pipeSize = pipeSize.trim(),
                        customInnerDiameter = customId.trim(),
                        customOuterDiameter = customOd.trim(),
                        customThickness = customThickness.trim(),
                        flangeFace = flangeFace,
                        boltHoles = boltHoles
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FlangeColors.PrimaryButton,
                contentColor = FlangeColors.PrimaryButtonText
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "Save Flange Form",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LabeledField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        content()
        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun DropdownField(
    value: String,
    options: List<DropdownOption>,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var anchorSize by remember { mutableStateOf(IntSize.Zero) }

    val displayText = if (value.isBlank()) {
        placeholder
    } else {
        options.firstOrNull { it.value == value }?.displayLabel ?: value
    }
    val displayColor = if (value.isBlank()) FlangeColors.TextMuted else FlangeColors.TextPrimary

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { anchorSize = it.size }
                .border(1.dp, FlangeColors.Divider, RoundedCornerShape(10.dp))
                .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(10.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = displayText,
                color = displayColor
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { anchorSize.width.toDp() })
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.menuLabel) },
                    onClick = {
                        onValueChange(option.value)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun gasketTypeOptions(): List<DropdownOption> = listOf(
    DropdownOption("Spiral-wound"),
    DropdownOption("Soft-faced metal core with facing layers such as flexible graphite, PTFE, or other conformable materials"),
    DropdownOption("Flexible graphite reinforced with a metal interlayer insert"),
    DropdownOption("Grooved metal"),
    DropdownOption("Flat solid metal"),
    DropdownOption("Flat metal jacketed"),
    DropdownOption("Soft cut sheet, thickness 1/16\""),
    DropdownOption("Soft cut sheet, thickness >1/16\"")
)

private fun flangeClassOptions(): List<DropdownOption> = listOf(
    DropdownOption("150#"),
    DropdownOption("300#"),
    DropdownOption("600#"),
    DropdownOption("900#"),
    DropdownOption("1500#"),
    DropdownOption("2500#")
)

private fun flangeFaceOptions(): List<DropdownOption> = listOf(
    DropdownOption("RF", "RF — Raised Face", "RF"),
    DropdownOption("FF", "FF — Flat Face", "FF"),
    DropdownOption("LJF", "LJF — Lap Joint Face (used with lap joint flanges + stub ends)", "LJF"),
    DropdownOption("RTJ", "RTJ — Ring-Type Joint (metal ring gasket)", "RTJ"),
    DropdownOption("TF", "TF — Tongue Face", "TF"),
    DropdownOption("GF", "GF — Groove Face", "GF"),
    DropdownOption("M&F", "M&F — Male & Female (matching set)", "M&F")
)

private fun boltHoleOptions(): List<DropdownOption> = (4..88 step 2).map { DropdownOption(it.toString()) }
