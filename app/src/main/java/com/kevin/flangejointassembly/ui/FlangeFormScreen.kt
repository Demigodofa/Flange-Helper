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
import androidx.compose.material3.Checkbox
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
    val displayLabel: String = value,
    val percentOfDry: Double? = null
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
    var wrenchSerials by remember { mutableStateOf("") }
    var wrenchCalDateMillis by remember { mutableLongStateOf(0L) }
    var torqueDry by remember { mutableStateOf(true) }
    var torqueWet by remember { mutableStateOf(false) }
    var lubricantType by remember { mutableStateOf("") }
    var flangeClass by remember { mutableStateOf("") }
    var pipeSize by remember { mutableStateOf("") }
    var customId by remember { mutableStateOf("") }
    var customOd by remember { mutableStateOf("") }
    var customThickness by remember { mutableStateOf("") }
    var flangeFace by remember { mutableStateOf("") }
    var boltHoles by remember { mutableStateOf("") }
    var flangeFaceCondition by remember { mutableStateOf("") }
    var flangeParallel by remember { mutableStateOf("") }
    var fastenerType by remember { mutableStateOf("") }
    var fastenerSpec by remember { mutableStateOf("") }
    var fastenerLength by remember { mutableStateOf("") }
    var fastenerDiameter by remember { mutableStateOf("") }
    var nutSpec by remember { mutableStateOf("") }
    var calculatedTargetTorque by remember { mutableStateOf("") }
    var specifiedTargetTorque by remember { mutableStateOf("") }
    var pass1Confirmed by remember { mutableStateOf(false) }
    var pass1Initials by remember { mutableStateOf("") }
    var pass2Confirmed by remember { mutableStateOf(false) }
    var pass2Initials by remember { mutableStateOf("") }
    var pass3Confirmed by remember { mutableStateOf(false) }
    var pass3Initials by remember { mutableStateOf("") }
    var pass4Confirmed by remember { mutableStateOf(false) }
    var pass4Initials by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showWrenchCalPicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    val wrenchCalPickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (wrenchCalDateMillis > 0) wrenchCalDateMillis else null
    )

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

    if (showWrenchCalPicker) {
        DatePickerDialog(
            onDismissRequest = { showWrenchCalPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    wrenchCalPickerState.selectedDateMillis?.let { wrenchCalDateMillis = it }
                    showWrenchCalPicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showWrenchCalPicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = wrenchCalPickerState)
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
            text = "FLANGE TORQUE REPORT",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
            ),
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "New Flange Bolting Form",
            style = MaterialTheme.typography.titleMedium,
            color = FlangeColors.TextSecondary
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Wrench(es) Ser. #('s)",
                    style = MaterialTheme.typography.labelLarge,
                    color = FlangeColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = wrenchSerials,
                    onValueChange = { wrenchSerials = it.take(25) },
                    singleLine = true,
                    placeholder = { Text("Up to 25 characters") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Wrench Cal. Date",
                    style = MaterialTheme.typography.labelLarge,
                    color = FlangeColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                val calDateText = if (wrenchCalDateMillis > 0) {
                    formatDate(wrenchCalDateMillis)
                } else {
                    ""
                }
                OutlinedTextField(
                    value = calDateText,
                    onValueChange = {},
                    readOnly = true,
                    singleLine = true,
                    trailingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Select calibration date"
                        )
                    },
                    placeholder = { Text("Select date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showWrenchCalPicker = true }
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = torqueDry,
                    onCheckedChange = {
                        torqueDry = it
                        if (it) torqueWet = false
                    }
                )
                Text("Dry Torque")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = torqueWet,
                    onCheckedChange = {
                        torqueWet = it
                        if (it) torqueDry = false
                    }
                )
                Text("Wet Torque")
            }
        }
        if (torqueWet) {
            Spacer(modifier = Modifier.height(8.dp))
            LabeledField(label = "Lubricant Type") {
                DropdownField(
                    value = lubricantType,
                    options = lubricantOptions(),
                    placeholder = "Select lubricant",
                    onValueChange = { lubricantType = it }
                )
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
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

        LabeledField(label = "Flange face free of scratches/nicks/gouges/burrs—especially radial damage") {
            DropdownField(
                value = flangeFaceCondition,
                options = yesRemediateOptions(),
                placeholder = "Select",
                onValueChange = { flangeFaceCondition = it }
            )
        }

        LabeledField(label = "Flange faces are Parallel and centered") {
            DropdownField(
                value = flangeParallel,
                options = yesRemediateOptions(),
                placeholder = "Select",
                onValueChange = { flangeParallel = it }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bolts or Studs",
                    style = MaterialTheme.typography.labelLarge,
                    color = FlangeColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                DropdownField(
                    value = fastenerType,
                    options = fastenerTypeOptions(),
                    placeholder = "Select",
                    onValueChange = { fastenerType = it }
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Spec.",
                    style = MaterialTheme.typography.labelLarge,
                    color = FlangeColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                DropdownField(
                    value = fastenerSpec,
                    options = fastenerSpecOptions(),
                    placeholder = "Select",
                    onValueChange = { fastenerSpec = it }
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Length",
                    style = MaterialTheme.typography.labelLarge,
                    color = FlangeColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = fastenerLength,
                    onValueChange = { fastenerLength = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Diameter",
                    style = MaterialTheme.typography.labelLarge,
                    color = FlangeColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                DropdownField(
                    value = fastenerDiameter,
                    options = fastenerDiameterOptions(),
                    placeholder = "Select",
                    onValueChange = { fastenerDiameter = it }
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))

        LabeledField(label = "Nuts") {
            DropdownField(
                value = nutSpec,
                options = nutSpecOptions(),
                placeholder = "Select",
                onValueChange = { nutSpec = it }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Torque",
            style = TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            ),
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        LabeledField(label = "Calculated Target Torque (ft-lb)") {
            OutlinedTextField(
                value = calculatedTargetTorque,
                onValueChange = { calculatedTargetTorque = it },
                singleLine = true,
                placeholder = { Text("Auto-calculated later") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        LabeledField(label = "Or Specified Target Torque (ft-lb)") {
            OutlinedTextField(
                value = specifiedTargetTorque,
                onValueChange = { specifiedTargetTorque = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        val baseTorque = specifiedTargetTorque.toDoubleOrNull()
            ?: calculatedTargetTorque.toDoubleOrNull()
            ?: 0.0
        val lube = lubricantOptions().firstOrNull { it.value == lubricantType }
        val lubePercent = lube?.percentOfDry ?: 1.0
        val effectiveTorque = if (torqueWet) baseTorque * lubePercent else baseTorque

        if (effectiveTorque > 0) {
            val effectiveLabel = if (torqueWet) {
                String.format("Effective Target Torque (wet): %.0f ft-lb", effectiveTorque)
            } else {
                String.format("Effective Target Torque: %.0f ft-lb", effectiveTorque)
            }
            Text(
                text = effectiveLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = FlangeColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        val pass1Low = effectiveTorque * 0.20
        val pass1High = effectiveTorque * 0.30
        val pass2Low = effectiveTorque * 0.50
        val pass2High = effectiveTorque * 0.70
        val pass3 = effectiveTorque

        TorquePassRow(
            label = "Pass #1. All bolts in sequence to 20% to 30% of the target torque.",
            valueText = if (effectiveTorque > 0) String.format("%.0f–%.0f ft-lb", pass1Low, pass1High) else "",
            checked = pass1Confirmed,
            onCheckedChange = { pass1Confirmed = it },
            initials = pass1Initials,
            onInitialsChange = { pass1Initials = it.take(4) }
        )

        TorquePassRow(
            label = "Pass #2. All bolts in sequence to 50% to 70% of the target torque.",
            valueText = if (effectiveTorque > 0) String.format("%.0f–%.0f ft-lb", pass2Low, pass2High) else "",
            checked = pass2Confirmed,
            onCheckedChange = { pass2Confirmed = it },
            initials = pass2Initials,
            onInitialsChange = { pass2Initials = it.take(4) }
        )

        TorquePassRow(
            label = "Pass #3. All bolts in sequence to 100% of the target torque.",
            valueText = if (effectiveTorque > 0) String.format("%.0f ft-lb", pass3) else "",
            checked = pass3Confirmed,
            onCheckedChange = { pass3Confirmed = it },
            initials = pass3Initials,
            onInitialsChange = { pass3Initials = it.take(4) }
        )

        TorquePassRow(
            label = "Check Pass. All bolts in circular order at 100% of target torque until there is no further nut rotation.",
            valueText = if (effectiveTorque > 0) String.format("%.0f ft-lb", pass3) else "",
            checked = pass4Confirmed,
            onCheckedChange = { pass4Confirmed = it },
            initials = pass4Initials,
            onInitialsChange = { pass4Initials = it.take(4) }
        )

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
                        wrenchSerials = wrenchSerials.trim(),
                        wrenchCalDateMillis = wrenchCalDateMillis,
                        torqueDry = torqueDry,
                        torqueWet = torqueWet,
                        lubricantType = lubricantType,
                        flangeClass = flangeClass,
                        pipeSize = pipeSize.trim(),
                        customInnerDiameter = customId.trim(),
                        customOuterDiameter = customOd.trim(),
                        customThickness = customThickness.trim(),
                        flangeFace = flangeFace,
                        boltHoles = boltHoles,
                        flangeFaceCondition = flangeFaceCondition,
                        flangeParallel = flangeParallel,
                        fastenerType = fastenerType,
                        fastenerSpec = fastenerSpec,
                        fastenerLength = fastenerLength.trim(),
                        fastenerDiameter = fastenerDiameter,
                        nutSpec = nutSpec,
                        calculatedTargetTorque = calculatedTargetTorque.trim(),
                        specifiedTargetTorque = specifiedTargetTorque.trim(),
                        pass1Confirmed = pass1Confirmed,
                        pass1Initials = pass1Initials.trim(),
                        pass2Confirmed = pass2Confirmed,
                        pass2Initials = pass2Initials.trim(),
                        pass3Confirmed = pass3Confirmed,
                        pass3Initials = pass3Initials.trim(),
                        pass4Confirmed = pass4Confirmed,
                        pass4Initials = pass4Initials.trim()
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

@Composable
private fun TorquePassRow(
    label: String,
    valueText: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    initials: String,
    onInitialsChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = FlangeColors.TextPrimary
        )
        if (valueText.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodySmall,
                color = FlangeColors.TextSecondary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
                Text("Yes")
            }
            OutlinedTextField(
                value = initials,
                onValueChange = onInitialsChange,
                singleLine = true,
                label = { Text("Initials") },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun gasketTypeOptions(): List<DropdownOption> = listOf(
    DropdownOption("Spiral-wound"),
    DropdownOption("Soft-faced metal core with facing layers such as flexible graphite, PTFE, or other conformable materials"),
    DropdownOption("Flexible graphite reinforced with a metal interlayer insert"),
    DropdownOption("Grooved metal"),
    DropdownOption("Flat solid metal"),
    DropdownOption("Flat metal jacketed"),
    DropdownOption("Soft cut sheet, thickness ≤1/16\""),
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

private fun yesRemediateOptions(): List<DropdownOption> = listOf(
    DropdownOption("Yes"),
    DropdownOption("Anything other than Yes Remediate the Problem and then Continue")
)

private fun fastenerTypeOptions(): List<DropdownOption> = listOf(
    DropdownOption("Bolts"),
    DropdownOption("Studs")
)

private fun fastenerSpecOptions(): List<DropdownOption> = listOf(
    DropdownOption("A193 B7"),
    DropdownOption("A193 B16"),
    DropdownOption("A193 B8 (304)"),
    DropdownOption("A193 B8M (316)"),
    DropdownOption("A320 L7"),
    DropdownOption("A193 B7M"),
    DropdownOption("A320 L7M"),
    DropdownOption("A453 Grade 660")
)

private fun fastenerDiameterOptions(): List<DropdownOption> = listOf(
    "1/2", "5/8", "3/4", "7/8", "1",
    "1-1/8", "1-1/4", "1-3/8", "1-1/2", "1-5/8",
    "1-3/4", "1-7/8", "2", "2-1/4", "2-1/2",
    "2-3/4", "3", "3-1/4", "3-1/2", "3-3/4", "4 (in.)"
).map { DropdownOption(it) }

private fun nutSpecOptions(): List<DropdownOption> = listOf(
    DropdownOption("A194 2H"),
    DropdownOption("A194 4"),
    DropdownOption("A194 8 (304)"),
    DropdownOption("A194 8M (316)"),
    DropdownOption("A194 2HM"),
    DropdownOption("A194 7"),
    DropdownOption("A194 7M")
)

private fun lubricantOptions(): List<DropdownOption> = listOf(
    DropdownOption(
        value = "Unlubricated (K 0.27)",
        menuLabel = "Unlubricated (K 0.27) — 100% of dry torque",
        percentOfDry = 1.0
    ),
    DropdownOption(
        value = "Moly paste (K 0.11)",
        menuLabel = "Moly paste (K 0.11) — ~41% of dry torque",
        percentOfDry = 0.41
    ),
    DropdownOption(
        value = "Never-Seez Regular (K 0.13)",
        menuLabel = "Never-Seez Regular (K 0.13) — ~48% of dry torque",
        percentOfDry = 0.48
    ),
    DropdownOption(
        value = "Copper/Nickel anti-seize (K 0.15)",
        menuLabel = "Copper/Nickel anti-seize (K 0.15) — ~56% of dry torque",
        percentOfDry = 0.56
    ),
    DropdownOption(
        value = "High-temp blends (K 0.17)",
        menuLabel = "High-temp blends (K 0.17) — ~63% of dry torque",
        percentOfDry = 0.63
    )
)
