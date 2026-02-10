package com.kevin.flangejointassembly.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import com.kevin.flangejointassembly.ui.components.FlangeHeader
import kotlin.math.roundToInt

private data class DropdownOption(
    val value: String,
    val menuLabel: String = value,
    val displayLabel: String = value,
    val percentOfDry: Double? = null,
    val nutFactorK: Double? = null
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

    val context = LocalContext.current
    var referenceData by remember { mutableStateOf<ReferenceData.Data?>(null) }

    LaunchedEffect(Unit) {
        referenceData = ReferenceData.load(context)
    }

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
    var fastenerClass by remember { mutableStateOf("") }
    var fastenerLength by remember { mutableStateOf("") }
    var fastenerDiameter by remember { mutableStateOf("") }
    var threadSeries by remember { mutableStateOf("") }
    var nutSpec by remember { mutableStateOf("") }
    var torqueMethod by remember { mutableStateOf("YIELD_PERCENT") }
    var targetBoltLoadF by remember { mutableStateOf("") }
    var pctYieldTarget by remember { mutableStateOf("0.50") }
    var pctYieldEdited by remember { mutableStateOf(false) }
    var workingTempF by remember { mutableStateOf("") }
    var usedTempF by remember { mutableStateOf("") }
    var calculatedTargetTorque by remember { mutableStateOf("") }
    var specifiedTargetTorque by remember { mutableStateOf("") }
    var calculatedEdited by remember { mutableStateOf(false) }
    var useCustomTorque by remember { mutableStateOf(false) }
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

    val diameterIn = parseDiameterInches(fastenerDiameter)
    val diameterKey = normalizeDiameterKey(fastenerDiameter)
    val defaultThreadSeries = defaultThreadSeriesFor(diameterIn)

    LaunchedEffect(diameterKey) {
        if (threadSeries.isBlank() && defaultThreadSeries.isNotBlank()) {
            threadSeries = defaultThreadSeries
        }
    }

    val tpi = referenceData?.tpiLookup?.get(threadSeries)?.get(diameterKey)
    val asLookup = referenceData?.asLookup?.get(threadSeries)?.get(diameterKey)
    val asIn2 = asLookup ?: calculateTensileStressArea(diameterIn, tpi)
    val gradeKey = gradeKeyForSpec(fastenerSpec, fastenerClass)
    val syKsi = gradeKey?.let { lookupSy(referenceData, it, diameterIn) }
    val selectedGasket = referenceData?.gasketTypes?.firstOrNull { it.label == gasketType }
    val requiresSpecifiedTorque = selectedGasket?.allowCalculatedTorque == false ||
        selectedGasket?.targetMethod == "SPECIFIED_TARGET_TORQUE_REQUIRED" ||
        selectedGasket?.defaults?.specifiedTargetTorqueRequired == true
    val tempOptions = remember(referenceData) { buildTemperatureOptions(referenceData) }
    val workingTemp = workingTempF.toIntOrNull()
    val allowable = lookupAllowableStress(referenceData, gradeKey, diameterIn, workingTemp)
    val strengthKsi = allowable?.s ?: syKsi
    val roundedTemp = allowable?.usedTemp
    val usingAllowable = allowable != null
    val boltHoleCount = boltHoles.toIntOrNull()
    val boltSequence = boltHoleCount?.let {
        referenceData?.boltSequenceLookup?.get(it) ?: generateBoltSequence(it)
    }.orEmpty()
    val numberingDirectionText = referenceData?.boltNumberingDirection?.takeIf { it.isNotBlank() } ?: "CW"

    LaunchedEffect(tempOptions) {
        if (workingTempF.isBlank() && tempOptions.isNotEmpty()) {
            workingTempF = tempOptions.first().value
        }
    }

    LaunchedEffect(gasketType, selectedGasket) {
        val defaultPct = selectedGasket?.defaults?.boltStressPctYieldDefault
        if (defaultPct != null && !pctYieldEdited) {
            pctYieldTarget = String.format("%.2f", defaultPct)
        }
        if (requiresSpecifiedTorque) {
            torqueMethod = "USER_INPUT"
            useCustomTorque = true
        }
    }

    LaunchedEffect(roundedTemp) {
        usedTempF = roundedTemp?.toString().orEmpty()
    }

    val methodIsUserInput = torqueMethod == "USER_INPUT"
    val pctYield = parsePercentValue(pctYieldTarget)
    val boltLoadF = when {
        methodIsUserInput -> targetBoltLoadF.toDoubleOrNull()
        asIn2 != null && strengthKsi != null && pctYield != null -> asIn2 * (strengthKsi * 1000.0) * pctYield
        else -> null
    }

    val lube = lubricantOptions().firstOrNull { it.value == lubricantType }
    val kUsed = if (torqueWet) lube?.nutFactorK else 0.27
    val calculatedTorque = if (boltLoadF != null && diameterIn != null && kUsed != null) {
        (kUsed * diameterIn * boltLoadF) / 12.0
    } else {
        null
    }

    val specifiedTorqueValue = specifiedTargetTorque.toDoubleOrNull()
    val usingSpecifiedTorque = specifiedTorqueValue != null && (useCustomTorque || requiresSpecifiedTorque)

    val calculationIssues = buildList {
        if (!usingSpecifiedTorque) {
            if (referenceData == null) add("Reference data not loaded")
            if (diameterIn == null) add("Diameter is required")
            if (threadSeries.isBlank()) add("Thread series is required")
            if (tpi == null) add("TPI not available for selected diameter/thread series")
            if (asIn2 == null) add("Tensile stress area unavailable for selected diameter/thread series")
            if (gradeKey == null) add("Bolt grade is required for calculation")
            if (strengthKsi == null) add("Strength not available for selected grade/diameter")
            if (usingAllowable && workingTemp == null) add("Working temperature is required")
            if (!requiresSpecifiedTorque && methodIsUserInput && targetBoltLoadF.toDoubleOrNull() == null) {
                add("Target bolt load F is required")
            }
            if (!requiresSpecifiedTorque && !methodIsUserInput && pctYield == null) {
                add("Percent yield is required")
            }
        }
        if (requiresSpecifiedTorque && specifiedTargetTorque.toDoubleOrNull() == null) {
            add("Specified target torque is required for this gasket type")
        }
        if (useCustomTorque && specifiedTargetTorque.toDoubleOrNull() == null) {
            add("Custom final torque is enabled, but no torque value is entered")
        }
        if (torqueWet && lube?.nutFactorK == null) add("Lubricant selection is required for wet torque")
    }

    LaunchedEffect(calculatedTorque, calculatedEdited, useCustomTorque) {
        if (!useCustomTorque && !calculatedEdited && calculatedTorque != null) {
            calculatedTargetTorque = calculatedTorque.roundToInt().toString()
        }
    }

    LaunchedEffect(fastenerSpec) {
        if (fastenerSpec != "A453 Grade 660") {
            fastenerClass = ""
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
                        contentDescription = "Select date",
                        modifier = Modifier.clickable { showDatePicker = true }
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
                options = gasketTypeOptions(referenceData),
                placeholder = "Select gasket type",
                onValueChange = { gasketType = it }
            )
        }
        if (selectedGasket != null) {
            val defaultPct = selectedGasket.defaults.boltStressPctYieldDefault
            val allowed = selectedGasket.defaults.boltStressPctYieldAllowed
            val pctSummary = if (defaultPct != null) {
                val allowedText = if (allowed.size >= 2) {
                    "Allowed ${formatPercent(allowed.minOrNull() ?: defaultPct)}-" +
                        "${formatPercent(allowed.maxOrNull() ?: defaultPct)}"
                } else {
                    ""
                }
                "Default bolt stress ${formatPercent(defaultPct)}. $allowedText".trim()
            } else {
                ""
            }
            val targetMethodText = when (selectedGasket.targetMethod) {
                "BOLT_STRESS_PCT_YIELD" -> "Target method: Bolt stress % of yield"
                "GASKET_STRESS" -> "Target method: Gasket stress (requires clamp load inputs)"
                "SPECIFIED_TARGET_TORQUE_REQUIRED" -> "Target method: Specified target torque required"
                else -> ""
            }
            val retorqueText = selectedGasket.retorque.recommended?.let { recommended ->
                val timing = selectedGasket.retorque.timing ?: ""
                if (recommended) "Retorque recommended: $timing" else "Retorque: not typical"
            } ?: ""
            val warningsText = if (selectedGasket.warnings.isNotEmpty()) {
                "Warnings: ${selectedGasket.warnings.joinToString(" ")}"
            } else {
                ""
            }
            if (targetMethodText.isNotBlank() || pctSummary.isNotBlank() || retorqueText.isNotBlank() || warningsText.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = listOf(targetMethodText, pctSummary, retorqueText, warningsText)
                        .filter { it.isNotBlank() }
                        .joinToString("\n"),
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
            if (!requiresSpecifiedTorque) {
                val defaultPctLabel = selectedGasket.defaults.boltStressPctYieldDefault
                    ?.let { String.format("%.2f", it) }
                val yieldLabel = if (defaultPctLabel != null) {
                    "Target bolt stress % of yield (default $defaultPctLabel)"
                } else {
                    "Target bolt stress % of yield"
                }
                LabeledField(label = yieldLabel) {
                    OutlinedTextField(
                        value = pctYieldTarget,
                        onValueChange = {
                            pctYieldTarget = it
                            pctYieldEdited = true
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
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
                            contentDescription = "Select calibration date",
                            modifier = Modifier.clickable { showWrenchCalPicker = true }
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
            val lubeNote = lube?.let {
                "Selected lube: K=${it.nutFactorK ?: "?"}, " +
                    "${formatPercent(it.percentOfDry ?: 1.0)} of dry torque"
            }
            if (!lubeNote.isNullOrBlank()) {
                Text(
                    text = lubeNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Dry torque: K=0.27 (100% of dry torque)",
                style = MaterialTheme.typography.bodySmall,
                color = FlangeColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
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
        if (boltHoleCount != null) {
            val directionWord = if (numberingDirectionText.uppercase() == "CCW") {
                "counterclockwise"
            } else {
                "clockwise"
            }
            LabeledField(label = "Tightening Order (Report)") {
                if (boltSequence.isEmpty()) {
                    Text(
                        text = "Sequence not available for this bolt count.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FlangeColors.TextSecondary
                    )
                } else {
                    SequenceBox(
                        text = buildReportLine(
                            boltCount = boltHoleCount,
                            directionWord = directionWord,
                            sequence = boltSequence
                        )
                    )
                }
            }
        }

        LabeledField(label = "Flange face free of scratches/nicks/gouges/burrs-especially radial damage") {
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

        if (fastenerSpec == "A453 Grade 660") {
            LabeledField(label = "A453 660 Class") {
                DropdownField(
                    value = fastenerClass,
                    options = fastenerClassOptions(),
                    placeholder = "Select class",
                    onValueChange = { fastenerClass = it }
                )
            }
        }

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

        LabeledField(label = "Thread Series") {
            DropdownField(
                value = threadSeries,
                options = threadSeriesOptions(),
                placeholder = "Select thread series",
                onValueChange = { threadSeries = it }
            )
        }

        LabeledField(label = "TPI (auto)") {
            OutlinedTextField(
                value = tpi?.toString().orEmpty(),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                placeholder = { Text("Select diameter + thread series") },
                modifier = Modifier.fillMaxWidth()
            )
        }

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

        LabeledField(label = "Working Temperature ( F)") {
            DropdownField(
                value = workingTempF,
                options = tempOptions,
                placeholder = "Select temperature",
                onValueChange = { workingTempF = it }
            )
            if (usedTempF.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Rounded to: $usedTempF F",
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.TextSecondary
                )
            }
        }

        if (requiresSpecifiedTorque) {
            Text(
                text = "Specified target torque is required for this gasket type.",
                style = MaterialTheme.typography.bodySmall,
                color = FlangeColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            LabeledField(label = "Torque Method") {
                DropdownField(
                    value = torqueMethod,
                    options = torqueMethodOptions(),
                    placeholder = "Select method",
                    onValueChange = { torqueMethod = it }
                )
            }

            if (torqueMethod == "USER_INPUT") {
                LabeledField(label = "Target Bolt Load F (lbf)") {
                    OutlinedTextField(
                        value = targetBoltLoadF,
                        onValueChange = { targetBoltLoadF = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        if (strengthKsi != null) {
            val strengthNote = if (usingAllowable && usedTempF.isNotBlank()) {
                "Using S = $strengthKsi ksi at $usedTempF F"
            } else {
                "Using room-temp Sy = $strengthKsi ksi"
            }
            Text(
                text = strengthNote,
                style = MaterialTheme.typography.bodySmall,
                color = FlangeColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        LabeledField(label = "Calculated Target Torque (ft-lb)") {
            OutlinedTextField(
                value = calculatedTargetTorque,
                onValueChange = {
                    calculatedTargetTorque = it
                    calculatedEdited = true
                },
                singleLine = true,
                placeholder = { Text("Auto-calculated later") },
                modifier = Modifier.fillMaxWidth()
            )
            if (calculationIssues.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = calculationIssues.joinToString(separator = "; "),
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.DeleteButton
                )
            } else if (calculatedTorque != null) {
                val methodLabel = if (requiresSpecifiedTorque) {
                    "Specified torque required"
                } else if (torqueMethod == "USER_INPUT") {
                    "F input"
                } else {
                    "Yield % ${formatPercent(pctYield)}"
                }
                val kLabel = if (torqueWet) {
                    "K=${lube?.nutFactorK ?: "?"}"
                } else {
                    "K=0.27"
                }
                val tpiLabel = tpi?.toString() ?: "n/a"
                val asLabel = asIn2?.let { String.format("%.4f", it) } ?: "n/a"
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Calc: $methodLabel, $kLabel, TPI $tpiLabel, As $asLabel in^2",
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.TextSecondary
                )
            }
        }

        LabeledField(label = "Specified Final Target Torque (ft-lb)") {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = useCustomTorque,
                        onCheckedChange = {
                            if (requiresSpecifiedTorque) {
                                useCustomTorque = true
                            } else {
                                useCustomTorque = it
                            }
                        }
                    )
                    Text("Use Custom Final Torque")
                }
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = specifiedTargetTorque,
                    onValueChange = { specifiedTargetTorque = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter final target torque") }
                )
                if (specifiedTorqueValue != null && !useCustomTorque && !requiresSpecifiedTorque) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Enable custom torque to use this value.",
                        style = MaterialTheme.typography.bodySmall,
                        color = FlangeColors.TextSecondary
                    )
                }
            }
        }

    val calculatedTorqueValue = calculatedTargetTorque.toDoubleOrNull()
        val effectiveTorque = when {
            usingSpecifiedTorque -> specifiedTorqueValue ?: 0.0
            calculatedTorqueValue != null -> calculatedTorqueValue
            else -> 0.0
        }

        if (effectiveTorque > 0) {
            val effectiveLabel = String.format("Final Target Torque: %.0f ft-lb", effectiveTorque)
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
            valueText = if (effectiveTorque > 0) String.format("%.0f-%.0f ft-lb", pass1Low, pass1High) else "",
            checked = pass1Confirmed,
            onCheckedChange = { pass1Confirmed = it },
            initials = pass1Initials,
            onInitialsChange = { pass1Initials = it.take(4) }
        )

        TorquePassRow(
            label = "Pass #2. All bolts in sequence to 50% to 70% of the target torque.",
            valueText = if (effectiveTorque > 0) String.format("%.0f-%.0f ft-lb", pass2Low, pass2High) else "",
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
                        fastenerClass = fastenerClass,
                        fastenerLength = fastenerLength.trim(),
                        fastenerDiameter = fastenerDiameter,
                        threadSeries = threadSeries,
                        nutSpec = nutSpec,
                        workingTempF = workingTempF,
                        roundedTempF = usedTempF,
                        torqueMethod = torqueMethod,
                        targetBoltLoadF = targetBoltLoadF.trim(),
                        pctYieldTarget = pctYieldTarget.trim(),
                        tpiUsed = tpi?.toString().orEmpty(),
                        asUsed = asIn2?.let { String.format("%.4f", it) }.orEmpty(),
                        strengthKsiUsed = strengthKsi?.toString().orEmpty(),
                        kUsed = kUsed?.toString().orEmpty(),
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

@Composable
private fun SequenceBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FlangeColors.Divider, RoundedCornerShape(10.dp))
            .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = FlangeColors.TextPrimary
        )
    }
}

private fun buildReportLine(
    boltCount: Int,
    directionWord: String,
    sequence: List<Int>
): String {
    val sequenceText = sequence.joinToString(", ")
    return "$boltCount bolt holes, numbered from 12 o'clock going $directionWord.\n" +
        "Tightening order: $sequenceText"
}

private fun generateBoltSequence(boltCount: Int): List<Int> {
    if (boltCount < 4 || boltCount % 2 != 0) return emptyList()
    val half = boltCount / 2
    var pow2 = 1
    var bits = 0
    while (pow2 < half) {
        pow2 = pow2 shl 1
        bits += 1
    }
    val order = mutableListOf<Int>()
    for (i in 0 until pow2) {
        val rev = reverseBits(i, bits)
        if (rev < half) {
            order.add(rev)
        }
    }
    val odds = order.map { 2 * it + 1 }
    val evens = order.map { 2 * it + 2 }
    return odds + evens
}

private fun reverseBits(value: Int, bitCount: Int): Int {
    var v = value
    var result = 0
    repeat(bitCount) {
        result = (result shl 1) or (v and 1)
        v = v shr 1
    }
    return result
}

private fun gasketTypeOptions(data: ReferenceData.Data?): List<DropdownOption> {
    val fromData = data?.gasketTypes
        ?.map { DropdownOption(it.label) }
        ?.distinctBy { it.value }
        ?.filter { it.value.isNotBlank() }
        .orEmpty()
    if (fromData.isNotEmpty()) {
        return fromData
    }
    return listOf(
        DropdownOption("Spiral-wound"),
        DropdownOption("Soft-faced metal core with facing layers such as flexible graphite, PTFE, or other conformable materials"),
        DropdownOption("Flexible graphite reinforced with a metal interlayer insert"),
        DropdownOption("Grooved metal"),
        DropdownOption("Flat solid metal"),
        DropdownOption("Flat metal jacketed"),
        DropdownOption("Soft cut sheet, thickness <=1/16\""),
        DropdownOption("Soft cut sheet, thickness >1/16\"")
    )
}

private fun flangeClassOptions(): List<DropdownOption> = listOf(
    DropdownOption("150#"),
    DropdownOption("300#"),
    DropdownOption("600#"),
    DropdownOption("900#"),
    DropdownOption("1500#"),
    DropdownOption("2500#")
)

private fun flangeFaceOptions(): List<DropdownOption> = listOf(
    DropdownOption("RF", "RF - Raised Face", "RF"),
    DropdownOption("FF", "FF - Flat Face", "FF"),
    DropdownOption("LJF", "LJF - Lap Joint Face (used with lap joint flanges + stub ends)", "LJF"),
    DropdownOption("RTJ", "RTJ - Ring-Type Joint (metal ring gasket)", "RTJ"),
    DropdownOption("TF", "TF - Tongue Face", "TF"),
    DropdownOption("GF", "GF - Groove Face", "GF"),
    DropdownOption("M&F", "M&F - Male & Female (matching set)", "M&F")
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

private fun fastenerClassOptions(): List<DropdownOption> = listOf(
    DropdownOption("A"),
    DropdownOption("B"),
    DropdownOption("C"),
    DropdownOption("D")
)

private fun threadSeriesOptions(): List<DropdownOption> = listOf(
    DropdownOption("UNC"),
    DropdownOption("UNF"),
    DropdownOption("8UN")
)

private fun fastenerDiameterOptions(): List<DropdownOption> = listOf(
    DropdownOption("1/2"),
    DropdownOption("5/8"),
    DropdownOption("3/4"),
    DropdownOption("7/8"),
    DropdownOption("1"),
    DropdownOption("1-1/8"),
    DropdownOption("1-1/4"),
    DropdownOption("1-3/8"),
    DropdownOption("1-1/2"),
    DropdownOption("1-5/8"),
    DropdownOption("1-3/4"),
    DropdownOption("1-7/8"),
    DropdownOption("2"),
    DropdownOption("2-1/4"),
    DropdownOption("2-1/2"),
    DropdownOption("2-3/4"),
    DropdownOption("3"),
    DropdownOption("3-1/4"),
    DropdownOption("3-1/2"),
    DropdownOption("3-3/4"),
    DropdownOption(value = "4", menuLabel = "4 (in.)", displayLabel = "4")
)

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
        menuLabel = "Unlubricated (K 0.27) - 100% of dry torque",
        percentOfDry = 1.0,
        nutFactorK = 0.27
    ),
    DropdownOption(
        value = "Moly paste (K 0.11)",
        menuLabel = "Moly paste (K 0.11) - ~41% of dry torque",
        percentOfDry = 0.41,
        nutFactorK = 0.11
    ),
    DropdownOption(
        value = "Never-Seez Regular (K 0.13)",
        menuLabel = "Never-Seez Regular (K 0.13) - ~48% of dry torque",
        percentOfDry = 0.48,
        nutFactorK = 0.13
    ),
    DropdownOption(
        value = "Copper/Nickel anti-seize (K 0.15)",
        menuLabel = "Copper/Nickel anti-seize (K 0.15) - ~56% of dry torque",
        percentOfDry = 0.56,
        nutFactorK = 0.15
    ),
    DropdownOption(
        value = "High-temp blends (K 0.17)",
        menuLabel = "High-temp blends (K 0.17) - ~63% of dry torque",
        percentOfDry = 0.63,
        nutFactorK = 0.17
    )
)

private fun torqueMethodOptions(): List<DropdownOption> = listOf(
    DropdownOption(value = "YIELD_PERCENT", menuLabel = "Calculate from yield", displayLabel = "Calculate from yield"),
    DropdownOption(value = "USER_INPUT", menuLabel = "Use F directly", displayLabel = "Use F directly")
)

private fun buildTemperatureOptions(data: ReferenceData.Data?): List<DropdownOption> {
    val maxTemp = data?.allowableStressLookup
        ?.values
        ?.flatMap { it }
        ?.flatMap { it.temps }
        ?.maxOfOrNull { it.tMax }
        ?: 1000
    val minTemp = 100
    val temps = (minTemp..maxTemp step 50).map { it.toString() }
    return temps.map { DropdownOption(it) }
}

private fun parsePercentValue(text: String): Double? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    val value = trimmed.toDoubleOrNull() ?: return null
    return if (value > 1.0) value / 100.0 else value
}

private fun formatPercent(value: Double?): String {
    return if (value == null) "n/a" else String.format("%.0f%%", value * 100.0)
}

private data class AllowableResult(
    val s: Double,
    val usedTemp: Int
)

private fun lookupAllowableStress(
    data: ReferenceData.Data?,
    gradeKey: String?,
    diameterIn: Double?,
    workingTempF: Int?
): AllowableResult? {
    if (data == null || gradeKey == null || diameterIn == null || workingTempF == null) return null
    val ranges = data.allowableStressLookup[gradeKey] ?: return null
    val range = ranges.firstOrNull { diameterIn >= it.diaMin && diameterIn <= it.diaMax } ?: return null

    val direct = range.temps.firstOrNull { workingTempF >= it.tMin && workingTempF <= it.tMax }
    if (direct != null) {
        return AllowableResult(s = direct.s, usedTemp = direct.tMax)
    }

    val next = range.temps
        .filter { it.tMax >= workingTempF }
        .minByOrNull { it.tMax }
        ?: return null

    return AllowableResult(s = next.s, usedTemp = next.tMax)
}

private fun parseDiameterInches(value: String): Double? {
    if (value.isBlank()) return null
    val normalized = value.replace(" (in.)", "").trim()
    return if (normalized.contains("-")) {
        val parts = normalized.split("-")
        if (parts.size == 2) {
            val whole = parts[0].toDoubleOrNull() ?: return null
            val frac = parseFraction(parts[1]) ?: return null
            whole + frac
        } else {
            normalized.toDoubleOrNull()
        }
    } else {
        parseFraction(normalized) ?: normalized.toDoubleOrNull()
    }
}

private fun parseFraction(value: String): Double? {
    return if (value.contains("/")) {
        val parts = value.split("/")
        if (parts.size == 2) {
            val num = parts[0].toDoubleOrNull() ?: return null
            val den = parts[1].toDoubleOrNull() ?: return null
            if (den == 0.0) null else num / den
        } else {
            null
        }
    } else {
        value.toDoubleOrNull()
    }
}

private fun normalizeDiameterKey(value: String): String {
    return value.replace(" (in.)", "").trim()
}

private fun defaultThreadSeriesFor(diameterIn: Double?): String {
    if (diameterIn == null) return ""
    return if (diameterIn >= 1.0) "8UN" else "UNC"
}

private fun calculateTensileStressArea(diameterIn: Double?, tpi: Double?): Double? {
    if (diameterIn == null || tpi == null || tpi == 0.0) return null
    val term = diameterIn - (0.9743 / tpi)
    return 0.7854 * term * term
}

private fun gradeKeyForSpec(spec: String, fastenerClass: String): String? {
    return when (spec) {
        "A193 B7" -> "A193_B7"
        "A193 B16" -> "A193_B16"
        "A193 B8 (304)" -> "A193_B8_Class1_304"
        "A193 B8M (316)" -> "A193_B8M_Class1_316"
        "A320 L7" -> "A320_L7"
        "A193 B7M" -> "A193_B7M"
        "A320 L7M" -> "A320_L7M"
        "A453 Grade 660" -> if (fastenerClass.isNotBlank()) "A453_660_Class${fastenerClass}" else null
        else -> null
    }
}

private fun lookupSy(data: ReferenceData.Data?, gradeKey: String, diameterIn: Double?): Double? {
    if (data == null || diameterIn == null) return null
    val ranges = data.strengthLookup[gradeKey] ?: return null
    return ranges.firstOrNull { diameterIn >= it.diaMin && diameterIn <= it.diaMax }?.sy
}


