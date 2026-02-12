package com.kevin.flangejointassembly.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.graphics.asImageBitmap
import com.kevin.flangejointassembly.ui.components.FlangeHeader
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.content.FileProvider
import kotlin.math.roundToInt
import java.io.File
import java.io.FileOutputStream
import android.graphics.BitmapFactory
import android.net.Uri

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
    initialForm: FlangeFormItem? = null,
    onSave: (FlangeFormItem) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    var referenceData by remember { mutableStateOf<ReferenceData.Data?>(null) }
    var nutPairingConfig by remember { mutableStateOf<NutPairingConfig.Config?>(null) }

    LaunchedEffect(Unit) {
        referenceData = ReferenceData.load(context)
        nutPairingConfig = NutPairingConfig.load(context)
    }

    val initial = initialForm
    var dateMillis by remember { mutableLongStateOf(initial?.dateMillis ?: jobDateMillis) }
    var description by remember { mutableStateOf(initial?.description.orEmpty()) }
    var serviceType by remember { mutableStateOf(initial?.serviceType.orEmpty()) }
    var gasketType by remember { mutableStateOf(initial?.gasketType.orEmpty()) }
    var wrenchSerials by remember { mutableStateOf(initial?.wrenchSerials.orEmpty()) }
    var wrenchCalDateMillis by remember { mutableLongStateOf(initial?.wrenchCalDateMillis ?: 0L) }
    var torqueDry by remember { mutableStateOf(initial?.torqueDry ?: true) }
    var torqueWet by remember { mutableStateOf(initial?.torqueWet ?: false) }
    var lubricantType by remember { mutableStateOf(initial?.lubricantType.orEmpty()) }
    var flangeClass by remember { mutableStateOf(initial?.flangeClass.orEmpty()) }
    var pipeSize by remember { mutableStateOf(initial?.pipeSize.orEmpty()) }
    var customId by remember { mutableStateOf(initial?.customInnerDiameter.orEmpty()) }
    var customOd by remember { mutableStateOf(initial?.customOuterDiameter.orEmpty()) }
    var customThickness by remember { mutableStateOf(initial?.customThickness.orEmpty()) }
    var flangeFace by remember { mutableStateOf(initial?.flangeFace.orEmpty()) }
    var boltHoles by remember { mutableStateOf(initial?.boltHoles.orEmpty()) }
    var flangeFaceCondition by remember { mutableStateOf(initial?.flangeFaceCondition.orEmpty()) }
    var flangeParallel by remember { mutableStateOf(initial?.flangeParallel.orEmpty()) }
    var fastenerType by remember { mutableStateOf(initial?.fastenerType.orEmpty()) }
    var fastenerSpec by remember { mutableStateOf(initial?.fastenerSpec.orEmpty()) }
    var fastenerClass by remember { mutableStateOf(initial?.fastenerClass.orEmpty()) }
    var fastenerLength by remember { mutableStateOf(initial?.fastenerLength.orEmpty()) }
    var fastenerDiameter by remember { mutableStateOf(initial?.fastenerDiameter.orEmpty()) }
    var threadSeries by remember { mutableStateOf(initial?.threadSeries.orEmpty()) }
    var nutSpec by remember { mutableStateOf(initial?.nutSpec.orEmpty()) }
    var nutOverrideAcknowledged by remember { mutableStateOf(initial?.nutOverrideAcknowledged ?: false) }
    var washerUsed by remember { mutableStateOf(initial?.washerUsed ?: false) }
    var targetBoltLoadF by remember { mutableStateOf(initial?.targetBoltLoadF.orEmpty()) }
    var pctYieldTarget by remember { mutableStateOf(initial?.pctYieldTarget ?: "0.50") }
    var pctYieldEdited by remember { mutableStateOf(false) }
    var workingTempF by remember { mutableStateOf(initial?.workingTempF.orEmpty()) }
    var usedTempF by remember { mutableStateOf(initial?.roundedTempF.orEmpty()) }
    var calculatedTargetTorque by remember { mutableStateOf(initial?.calculatedTargetTorque.orEmpty()) }
    var specifiedTargetTorque by remember { mutableStateOf(initial?.specifiedTargetTorque.orEmpty()) }
    var calculatedEdited by remember { mutableStateOf(false) }
    var useCustomTorque by remember { mutableStateOf(false) }
    val photoUris = remember { mutableStateListOf<String>() }
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }
    var showPhotoReview by remember { mutableStateOf(false) }
    var showMaxPhotosDialog by remember { mutableStateOf(false) }
    var showDeletePhotoDialog by remember { mutableStateOf(false) }
    var deletePhotoIndex by remember { mutableStateOf(-1) }
    var contractorPrintName by remember { mutableStateOf(initial?.contractorPrintName.orEmpty()) }
    var contractorSignUri by remember { mutableStateOf(initial?.contractorSignUri.orEmpty()) }
    var contractorDateMillis by remember { mutableLongStateOf(initial?.contractorDateMillis ?: 0L) }
    var facilityPrintName by remember { mutableStateOf(initial?.facilityPrintName.orEmpty()) }
    var facilitySignUri by remember { mutableStateOf(initial?.facilitySignUri.orEmpty()) }
    var facilityDateMillis by remember { mutableLongStateOf(initial?.facilityDateMillis ?: 0L) }
    var showContractorDatePicker by remember { mutableStateOf(false) }
    var showFacilityDatePicker by remember { mutableStateOf(false) }
    var showSignatureDialog by remember { mutableStateOf(false) }
    var signatureTarget by remember { mutableStateOf(SignatureTarget.Contractor) }
    var pass1Confirmed by remember { mutableStateOf(initial?.pass1Confirmed ?: false) }
    var pass1Initials by remember { mutableStateOf(initial?.pass1Initials.orEmpty()) }
    var pass2Confirmed by remember { mutableStateOf(initial?.pass2Confirmed ?: false) }
    var pass2Initials by remember { mutableStateOf(initial?.pass2Initials.orEmpty()) }
    var pass3Confirmed by remember { mutableStateOf(initial?.pass3Confirmed ?: false) }
    var pass3Initials by remember { mutableStateOf(initial?.pass3Initials.orEmpty()) }
    var pass4Confirmed by remember { mutableStateOf(initial?.pass4Confirmed ?: false) }
    var pass4Initials by remember { mutableStateOf(initial?.pass4Initials.orEmpty()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showWrenchCalPicker by remember { mutableStateOf(false) }
    var showNutOverride by remember { mutableStateOf(false) }
    var showWasherNotes by remember { mutableStateOf(false) }
    var photoSessionActive by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    val wrenchCalPickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (wrenchCalDateMillis > 0) wrenchCalDateMillis else null
    )
    val contractorDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (contractorDateMillis > 0) contractorDateMillis else null
    )
    val facilityDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (facilityDateMillis > 0) facilityDateMillis else null
    )

    LaunchedEffect(initial?.id) {
        photoUris.clear()
        photoUris.addAll(initial?.photoUris.orEmpty())
        useCustomTorque = initial?.specifiedTargetTorque?.isNotBlank() == true
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { dateMillis = normalizePickerMillis(it) }
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
                    wrenchCalPickerState.selectedDateMillis?.let { wrenchCalDateMillis = normalizePickerMillis(it) }
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

    if (showContractorDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showContractorDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    contractorDatePickerState.selectedDateMillis?.let { contractorDateMillis = normalizePickerMillis(it) }
                    showContractorDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showContractorDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = contractorDatePickerState)
        }
    }

    if (showFacilityDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showFacilityDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    facilityDatePickerState.selectedDateMillis?.let { facilityDateMillis = normalizePickerMillis(it) }
                    showFacilityDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFacilityDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = facilityDatePickerState)
        }
    }

    if (showMaxPhotosDialog) {
        AlertDialog(
            onDismissRequest = { showMaxPhotosDialog = false },
            title = { Text("Max photos taken") },
            text = { Text("You have taken 4 photos. Exit now?") },
            confirmButton = {
                TextButton(onClick = {
                    showMaxPhotosDialog = false
                    photoSessionActive = false
                }) {
                    Text("Exit")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showMaxPhotosDialog = false
                    photoSessionActive = false
                }) {
                    Text("Keep")
                }
            }
        )
    }

    if (showDeletePhotoDialog && deletePhotoIndex in photoUris.indices) {
        AlertDialog(
            onDismissRequest = { showDeletePhotoDialog = false },
            title = { Text("Delete Photo") },
            text = { Text("Are you sure you want to delete this photo?") },
            confirmButton = {
                TextButton(onClick = {
                    val uriString = photoUris[deletePhotoIndex]
                    deletePhotoIndex = -1
                    showDeletePhotoDialog = false
                    photoUris.remove(uriString)
                    runCatching {
                        val uri = Uri.parse(uriString)
                        if (uri.scheme == "content") {
                            context.contentResolver.delete(uri, null, null)
                        }
                    }
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    deletePhotoIndex = -1
                    showDeletePhotoDialog = false
                }) {
                    Text("No")
                }
            }
        )
    }

    if (showSignatureDialog) {
        SignatureDialog(
            title = if (signatureTarget == SignatureTarget.Contractor) {
                "Contractor Signature"
            } else {
                "Facility Signature"
            },
            onSave = { bitmap ->
                val uriString = saveSignatureBitmap(context, bitmap)
                if (signatureTarget == SignatureTarget.Contractor) {
                    contractorSignUri = uriString
                } else {
                    facilitySignUri = uriString
                }
                showSignatureDialog = false
            },
            onDismiss = { showSignatureDialog = false }
        )
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingPhotoUri != null) {
            showPhotoReview = true
        } else {
            pendingPhotoPath?.let { File(it).delete() }
            pendingPhotoUri = null
            pendingPhotoPath = null
            photoSessionActive = false
        }
    }

    fun launchCamera() {
        if (photoUris.size >= 4) {
            showMaxPhotosDialog = true
            photoSessionActive = false
            return
        }
        val photosDir = File(context.filesDir, "flange_helper/photos")
        if (!photosDir.exists()) {
            photosDir.mkdirs()
        }
        val file = File(photosDir, "photo_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        pendingPhotoUri = uri
        pendingPhotoPath = file.absolutePath
        takePictureLauncher.launch(uri)
    }

    if (showPhotoReview && pendingPhotoUri != null) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Review Photo") },
            text = {
                PhotoPreview(uri = pendingPhotoUri!!)
            },
            confirmButton = {
                TextButton(onClick = {
                    pendingPhotoUri?.let { photoUris.add(it.toString()) }
                    pendingPhotoUri = null
                    pendingPhotoPath = null
                    showPhotoReview = false
                    if (photoUris.size >= 4) {
                        showMaxPhotosDialog = true
                        photoSessionActive = false
                    } else if (photoSessionActive) {
                        launchCamera()
                    }
                }) {
                    Text("Keep")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = {
                        pendingPhotoPath?.let { File(it).delete() }
                        pendingPhotoUri = null
                        pendingPhotoPath = null
                        showPhotoReview = false
                        launchCamera()
                    }) {
                        Text("Retake")
                    }
                    TextButton(onClick = {
                        pendingPhotoPath?.let { File(it).delete() }
                        pendingPhotoUri = null
                        pendingPhotoPath = null
                        showPhotoReview = false
                        photoSessionActive = false
                    }) {
                        Text("Exit")
                    }
                }
            }
        )
    }

    val diameterIn = FlangeMath.parseDiameterInches(fastenerDiameter)
    val diameterKey = FlangeMath.normalizeDiameterKey(fastenerDiameter)
    val defaultThreadSeries = FlangeMath.defaultThreadSeriesFor(diameterIn)

    LaunchedEffect(diameterKey) {
        if (threadSeries.isBlank() && defaultThreadSeries.isNotBlank()) {
            threadSeries = defaultThreadSeries
        }
    }

    val tpi = referenceData?.tpiLookup?.get(threadSeries)?.get(diameterKey)
    val asLookup = referenceData?.asLookup?.get(threadSeries)?.get(diameterKey)
    val asIn2 = asLookup ?: FlangeMath.calculateTensileStressArea(diameterIn, tpi)
    val gradeKey = gradeKeyForSpec(fastenerSpec, fastenerClass)
    val syKsi = gradeKey?.let { FlangeMath.lookupSy(referenceData, it, diameterIn) }
    val selectedGasket = referenceData?.gasketTypes?.firstOrNull { it.label == gasketType }
    val requiresSpecifiedTorque = selectedGasket?.allowCalculatedTorque == false ||
        selectedGasket?.targetMethod == "SPECIFIED_TARGET_TORQUE_REQUIRED" ||
        selectedGasket?.defaults?.specifiedTargetTorqueRequired == true
    val workingTemp = 100
    val allowable = FlangeMath.lookupAllowableStress(referenceData, gradeKey, diameterIn, workingTemp)
    val strengthKsi = allowable?.s ?: syKsi
    val roundedTemp = allowable?.usedTemp
    val boltHoleCount = boltHoles.toIntOrNull()
    val boltSequence = boltHoleCount?.let {
        referenceData?.boltSequenceLookup?.get(it) ?: FlangeMath.generateBoltSequence(it)
    }.orEmpty()
    val numberingDirectionText = referenceData?.boltNumberingDirection?.takeIf { it.isNotBlank() } ?: "CW"
    val boltSpecKey = mapBoltSpecKey(fastenerSpec, fastenerClass)
    val nutKey = mapNutKey(nutSpec)
    val nutPairingEvaluation = NutPairingConfig.evaluate(nutPairingConfig, boltSpecKey, nutKey)
    val requiresNutAck = nutPairingEvaluation?.requiresAck == true
    val washerMessages = remember(boltSpecKey, nutKey, washerUsed) {
        buildWasherMessages(
            boltSpecKey = boltSpecKey,
            nutKey = nutKey,
            washerUsed = washerUsed
        )
    }

    LaunchedEffect(washerMessages) {
        if (washerMessages.isEmpty()) {
            showWasherNotes = false
        }
    }

    LaunchedEffect(referenceData) {
        if (workingTempF.isBlank()) {
            workingTempF = "100"
        }
        if (usedTempF.isBlank()) {
            usedTempF = "100"
        }
    }

    LaunchedEffect(gasketType, selectedGasket) {
        val defaultPct = selectedGasket?.defaults?.boltStressPctYieldDefault
        if (defaultPct != null && !pctYieldEdited) {
            pctYieldTarget = String.format("%.2f", defaultPct)
        }
        if (requiresSpecifiedTorque) {
            useCustomTorque = true
        }
    }

    LaunchedEffect(requiresNutAck) {
        if (!requiresNutAck) {
            nutOverrideAcknowledged = false
            showNutOverride = false
        }
    }

    LaunchedEffect(roundedTemp) {
        usedTempF = roundedTemp?.toString().orEmpty()
    }

    val methodIsUserInput = targetBoltLoadF.toDoubleOrNull() != null
    val torqueMethodValue = when {
        requiresSpecifiedTorque || useCustomTorque -> "SPECIFIED_TORQUE"
        methodIsUserInput -> "USER_INPUT"
        else -> "YIELD_PERCENT"
    }
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
        if (requiresNutAck && !nutOverrideAcknowledged) {
            add("Nut mismatch requires acknowledgement before saving")
        }
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
            LabeledField(label = "Bolt Marking / Tightening") {
                val text = buildReportLine(
                    boltCount = boltHoleCount,
                    directionWord = directionWord,
                    sequence = boltSequence
                )
                SequenceBox(text = text)
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

        if (nutPairingEvaluation != null) {
            if (nutPairingEvaluation.recommended.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recommended Nuts",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    color = FlangeColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    nutPairingEvaluation.recommended.forEach { rec ->
                        Text(
                            text = "• ${mapNutLabel(rec.nut)} — ${rec.label}",
                            style = MaterialTheme.typography.bodySmall,
                            color = FlangeColors.TextSecondary
                        )
                    }
                }
            }

            if (nutPairingEvaluation.warnings.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Nut Warnings",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                    color = FlangeColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    nutPairingEvaluation.warnings.forEach { warn ->
                        val color = when (warn.severity.lowercase()) {
                            "high" -> FlangeColors.DeleteButton
                            "warn" -> Color(0xFFB45309)
                            else -> FlangeColors.TextSecondary
                        }
                        Text(
                            text = warn.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = color
                        )
                    }
                }
            }

            if (requiresNutAck) {
                Spacer(modifier = Modifier.height(8.dp))
                if (!showNutOverride) {
                    Button(
                        onClick = { showNutOverride = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FlangeColors.EditButton,
                            contentColor = FlangeColors.EditButtonText
                        )
                    ) {
                        Text("Proceed anyway (facility hardware)")
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = nutOverrideAcknowledged,
                            onCheckedChange = { nutOverrideAcknowledged = it }
                        )
                        Text("Acknowledged")
                    }
                    if (!nutOverrideAcknowledged) {
                        Text(
                            text = "Acknowledgement required to save.",
                            style = MaterialTheme.typography.bodySmall,
                            color = FlangeColors.DeleteButton
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        LabeledField(label = "Washer used?") {
            DropdownField(
                value = if (washerUsed) "Yes" else "No",
                options = washerOptions(),
                placeholder = "Select",
                onValueChange = { washerUsed = it == "Yes" }
            )
        }
        Text(
            text = "If unsure, select Yes (hardened flat washer recommended).",
            style = MaterialTheme.typography.bodySmall,
            color = FlangeColors.TextMuted
        )

        if (washerMessages.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Notes / Warnings",
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(8.dp))
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Open notes",
                    tint = Color(0xFFB45309),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable { showWasherNotes = true }
                )
            }
            if (showWasherNotes) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = FlangeColors.CardBackground),
                    border = BorderStroke(1.dp, FlangeColors.Divider)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notes / Warnings",
                                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
                                color = FlangeColors.TextPrimary
                            )
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close notes",
                                tint = FlangeColors.TextMuted,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { showWasherNotes = false }
                            )
                        }
                        washerMessages.forEach { message ->
                            val color = when (message.severity) {
                                WasherSeverity.WARN -> Color(0xFFB45309)
                                WasherSeverity.HIGH -> FlangeColors.DeleteButton
                                WasherSeverity.INFO -> FlangeColors.TextSecondary
                            }
                            Text(
                                text = message.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = color
                            )
                        }
                    }
                }
            }
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

        if (requiresSpecifiedTorque) {
            Text(
                text = "Specified target torque is required for this gasket type.",
                style = MaterialTheme.typography.bodySmall,
                color = FlangeColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))
        } else {
            LabeledField(label = "Target Bolt Load F (lbf)") {
                OutlinedTextField(
                    value = targetBoltLoadF,
                    onValueChange = { targetBoltLoadF = it },
                    singleLine = true,
                    placeholder = { Text("Leave blank to calculate from yield %") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (strengthKsi != null) {
            Text(
                text = "Using S = $strengthKsi ksi",
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
                val methodLabel = if (requiresSpecifiedTorque || useCustomTorque) {
                    "Specified torque"
                } else if (methodIsUserInput) {
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

        Text(
            text = "Take up to 4 Photos",
            style = MaterialTheme.typography.titleMedium,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                photoSessionActive = true
                launchCamera()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = FlangeColors.PrimaryButton,
                contentColor = FlangeColors.PrimaryButtonText
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            val remaining = 4 - photoUris.size
            val label = if (remaining > 0) "Add Photo ($remaining remaining)" else "Max photos taken"
            Text(label)
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (photoUris.isEmpty()) {
            Text(
                text = "No photos yet.",
                style = MaterialTheme.typography.bodySmall,
                color = FlangeColors.TextMuted
            )
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                photoUris.forEachIndexed { index, uriString ->
                    PhotoThumbnail(
                        uriString = uriString,
                        onDelete = {
                            deletePhotoIndex = index
                            showDeletePhotoDialog = true
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Contractor Representative",
            style = MaterialTheme.typography.titleMedium,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = contractorPrintName,
            onValueChange = { contractorPrintName = it },
            singleLine = true,
            label = { Text("Print") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    signatureTarget = SignatureTarget.Contractor
                    showSignatureDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FlangeColors.PrimaryButton,
                    contentColor = FlangeColors.PrimaryButtonText
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Sign")
            }
            if (contractorSignUri.isNotBlank()) {
                SignaturePreview(uriString = contractorSignUri)
                TextButton(onClick = { contractorSignUri = "" }) {
                    Text("Clear")
                }
            } else {
                Text(
                    text = "Signature line",
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.TextMuted
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = if (contractorDateMillis > 0) formatDate(contractorDateMillis) else "",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Date") },
            trailingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Select date",
                    modifier = Modifier.clickable { showContractorDatePicker = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showContractorDatePicker = true }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Facility Representative",
            style = MaterialTheme.typography.titleMedium,
            color = FlangeColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = facilityPrintName,
            onValueChange = { facilityPrintName = it },
            singleLine = true,
            label = { Text("Print") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    signatureTarget = SignatureTarget.Facility
                    showSignatureDialog = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = FlangeColors.PrimaryButton,
                    contentColor = FlangeColors.PrimaryButtonText
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Sign")
            }
            if (facilitySignUri.isNotBlank()) {
                SignaturePreview(uriString = facilitySignUri)
                TextButton(onClick = { facilitySignUri = "" }) {
                    Text("Clear")
                }
            } else {
                Text(
                    text = "Signature line",
                    style = MaterialTheme.typography.bodySmall,
                    color = FlangeColors.TextMuted
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = if (facilityDateMillis > 0) formatDate(facilityDateMillis) else "",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text("Date") },
            trailingIcon = {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Select date",
                    modifier = Modifier.clickable { showFacilityDatePicker = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showFacilityDatePicker = true }
        )

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            enabled = !requiresNutAck || nutOverrideAcknowledged,
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
                        nutOverrideAcknowledged = nutOverrideAcknowledged,
                        washerUsed = washerUsed,
                        workingTempF = workingTempF,
                        roundedTempF = usedTempF,
                        torqueMethod = torqueMethodValue,
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
                        pass4Initials = pass4Initials.trim(),
                        photoUris = photoUris.toList(),
                        contractorPrintName = contractorPrintName.trim(),
                        contractorSignUri = contractorSignUri,
                        contractorDateMillis = contractorDateMillis,
                        facilityPrintName = facilityPrintName.trim(),
                        facilitySignUri = facilitySignUri,
                        facilityDateMillis = facilityDateMillis
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
        Spacer(modifier = Modifier.height(80.dp))
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

@Composable
private fun PhotoPreview(uri: Uri) {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(uri) {
        bitmap = loadBitmapFromUri(context, uri.toString())
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Captured photo",
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        )
    } else {
        Text(
            text = "Loading photo...",
            style = MaterialTheme.typography.bodySmall,
            color = FlangeColors.TextSecondary
        )
    }
}

@Composable
private fun PhotoThumbnail(
    uriString: String,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val uri = remember(uriString) { Uri.parse(uriString) }
    var bitmap by remember(uriString) { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(uriString) {
        bitmap = loadBitmapFromUri(context, uri.toString())
    }

    Box(
        modifier = Modifier
            .width(96.dp)
            .height(96.dp)
            .border(1.dp, FlangeColors.Divider, RoundedCornerShape(10.dp))
            .background(Color.White, RoundedCornerShape(10.dp))
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Photo thumbnail",
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .background(Color(0xCCFFFFFF), RoundedCornerShape(10.dp))
                .clickable { onDelete() }
                .padding(2.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Delete photo",
                tint = FlangeColors.DeleteButton
            )
        }
    }
}

@Composable
private fun SignaturePreview(uriString: String) {
    val context = LocalContext.current
    var bitmap by remember(uriString) { mutableStateOf<android.graphics.Bitmap?>(null) }

    LaunchedEffect(uriString) {
        bitmap = loadBitmapFromUri(context, uriString)
    }

    Box(
        modifier = Modifier
            .width(140.dp)
            .height(60.dp)
            .border(1.dp, FlangeColors.Divider, RoundedCornerShape(10.dp))
            .background(Color.White, RoundedCornerShape(10.dp))
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Signature",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "Signed",
                style = MaterialTheme.typography.bodySmall,
                color = FlangeColors.TextSecondary,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private fun loadBitmapFromUri(context: android.content.Context, uriString: String): android.graphics.Bitmap? {
    return runCatching {
        val uri = Uri.parse(uriString)
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
        }
    }.getOrNull()
}

private fun saveSignatureBitmap(context: android.content.Context, bitmap: android.graphics.Bitmap): String {
    val dir = File(context.filesDir, "flange_helper/signatures")
    if (!dir.exists()) {
        dir.mkdirs()
    }
    val file = File(dir, "sign_${System.currentTimeMillis()}.png")
    FileOutputStream(file).use { output ->
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output)
    }
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    return uri.toString()
}

@Composable
private fun SignatureDialog(
    title: String,
    onSave: (android.graphics.Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    var strokes by remember { mutableStateOf(listOf<List<Offset>>()) }
    var currentStroke by remember { mutableStateOf(listOf<Offset>()) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }

    AlertDialog(
        onDismissRequest = { },
        title = { Text(title) },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .border(1.dp, FlangeColors.Divider, RoundedCornerShape(10.dp))
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .onGloballyPositioned { canvasSize = it.size }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentStroke = listOf(offset)
                            },
                            onDrag = { change, _ ->
                                currentStroke = currentStroke + change.position
                            },
                            onDragEnd = {
                                if (currentStroke.isNotEmpty()) {
                                    strokes = strokes + listOf(currentStroke)
                                    currentStroke = emptyList()
                                }
                            }
                        )
                    }
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val allStrokes = if (currentStroke.isNotEmpty()) strokes + listOf(currentStroke) else strokes
                    allStrokes.forEach { stroke ->
                        for (i in 0 until stroke.size - 1) {
                            drawLine(
                                color = Color.Black,
                                start = stroke[i],
                                end = stroke[i + 1],
                                strokeWidth = 4f,
                                cap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalized = if (currentStroke.isNotEmpty()) strokes + listOf(currentStroke) else strokes
                    if (finalized.isEmpty()) return@TextButton
                    val bitmap = renderSignatureBitmap(finalized, canvasSize)
                    onSave(bitmap)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = {
                    strokes = emptyList()
                    currentStroke = emptyList()
                }) {
                    Text("Clear")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

private fun renderSignatureBitmap(strokes: List<List<Offset>>, size: IntSize): android.graphics.Bitmap {
    val scale = 3
    val width = (size.width.coerceAtLeast(1)) * scale
    val height = (size.height.coerceAtLeast(1)) * scale
    val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        strokeWidth = 12f
        style = android.graphics.Paint.Style.STROKE
        strokeJoin = android.graphics.Paint.Join.ROUND
        strokeCap = android.graphics.Paint.Cap.ROUND
        isAntiAlias = true
    }
    strokes.forEach { stroke ->
        for (i in 0 until stroke.size - 1) {
            val start = stroke[i]
            val end = stroke[i + 1]
            canvas.drawLine(start.x * scale, start.y * scale, end.x * scale, end.y * scale, paint)
        }
    }
    return bitmap
}

private enum class SignatureTarget {
    Contractor,
    Facility
}

private fun buildReportLine(
    boltCount: Int,
    directionWord: String,
    sequence: List<Int>
): String {
    val markingOrder = if (sequence.isNotEmpty()) {
        sequence.joinToString(", ")
    } else {
        (1..boltCount).joinToString(", ")
    }
    return "$boltCount bolt holes starting from approx. 12 o'clock going $directionWord, " +
        "mark each bolt in this order: $markingOrder.\n" +
        "Tightening order: sequential 1, 2, 3, 4 ..."
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
    DropdownOption("A194 4M"),
    DropdownOption("A194 8 (304)"),
    DropdownOption("A194 8M (316)"),
    DropdownOption("A194 2HM"),
    DropdownOption("A194 7"),
    DropdownOption("A194 7M")
)

private fun mapNutKey(value: String): String? {
    return when (value) {
        "A194 2H" -> "A194_2H"
        "A194 2HM" -> "A194_2HM"
        "A194 4" -> "A194_4"
        "A194 4M" -> "A194_4M"
        "A194 7" -> "A194_7"
        "A194 7M" -> "A194_7M"
        "A194 8 (304)" -> "A194_8_304"
        "A194 8M (316)" -> "A194_8M_316"
        else -> null
    }
}

private fun mapNutLabel(nutKey: String): String {
    return when (nutKey) {
        "A194_2H" -> "A194 2H"
        "A194_2HM" -> "A194 2HM"
        "A194_4" -> "A194 4"
        "A194_4M" -> "A194 4M"
        "A194_7" -> "A194 7"
        "A194_7M" -> "A194 7M"
        "A194_8_304" -> "A194 8 (304)"
        "A194_8M_316" -> "A194 8M (316)"
        else -> nutKey
    }
}

private fun mapBoltSpecKey(spec: String, fastenerClass: String): BoltSpecKey? {
    return when (spec) {
        "A193 B7" -> BoltSpecKey("SA-193", "B7", null)
        "A193 B7M" -> BoltSpecKey("SA-193", "B7M", null)
        "A193 B16" -> BoltSpecKey("SA-193", "B16", null)
        "A193 B8 (304)" -> BoltSpecKey("SA-193", "B8", null)
        "A193 B8M (316)" -> BoltSpecKey("SA-193", "B8M", null)
        "A320 L7" -> BoltSpecKey("SA-320", "L7", null)
        "A320 L7M" -> BoltSpecKey("SA-320", "L7M", null)
        "A453 Grade 660" -> {
            if (fastenerClass.isBlank()) null else BoltSpecKey("SA-453", "660", "Class ${fastenerClass}")
        }
        else -> null
    }
}

private data class WasherMessage(
    val message: String,
    val severity: WasherSeverity
)

private enum class WasherSeverity { HIGH, WARN, INFO }

private fun washerOptions(): List<DropdownOption> = listOf(
    DropdownOption("No"),
    DropdownOption("Yes")
)

private fun isBoltStainless(boltSpecKey: BoltSpecKey?): Boolean {
    return when (boltSpecKey?.grade) {
        "B8", "B8M" -> true
        else -> false
    }
}

private fun isNutStainless(nutKey: String?): Boolean {
    return when (nutKey) {
        "A194_8_304", "A194_8M_316" -> true
        else -> false
    }
}

private fun isHighStrengthAlloy(boltSpecKey: BoltSpecKey?): Boolean {
    return when (boltSpecKey?.grade) {
        "B7", "B16", "L7", "L7M", "B7M" -> true
        else -> false
    }
}

private fun buildWasherMessages(
    boltSpecKey: BoltSpecKey?,
    nutKey: String?,
    washerUsed: Boolean,
    bearingSurfaceCondition: String = "unknown",
    stainlessWasherAvailable: Boolean = false
): List<WasherMessage> {
    val messages = mutableListOf<WasherMessage>()
    val stainlessPresent = isBoltStainless(boltSpecKey) || isNutStainless(nutKey)
    val highStrengthAlloy = isHighStrengthAlloy(boltSpecKey)

    if (stainlessPresent) {
        messages.add(
            WasherMessage(
                "Stainless fasteners are prone to galling causing incorrect inflated false torque readings. It is suggested to always use lubricant with stainless fastners.",
                WasherSeverity.WARN
            )
        )
        if (!washerUsed) {
            messages.add(
                WasherMessage(
                    if (stainlessWasherAvailable) "Use Stainless Flat washer + lubricant" else "Use Hardened Flat washer + lubricant",
                    WasherSeverity.WARN
                )
            )
        }
        messages.add(
            WasherMessage(
                "For stainless bolting, washers + lubricant help reduce galling at the nut bearing surface.",
                WasherSeverity.INFO
            )
        )
        messages.add(
            WasherMessage(
                "Mixed stainless and carbon steel contact can increase corrosion risk in wet/salty environments. Verify facility corrosion/spec requirements.",
                WasherSeverity.INFO
            )
        )
    }

    if (highStrengthAlloy &&
        !washerUsed &&
        bearingSurfaceCondition in setOf("unknown", "painted", "rough")
    ) {
        messages.add(
            WasherMessage(
                "A hardened flat washer can improve torque consistency by reducing embedment and providing a smoother bearing surface.",
                WasherSeverity.INFO
            )
        )
    }

    return messages.distinctBy { it.message }
}

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

private fun parsePercentValue(text: String): Double? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    val value = trimmed.toDoubleOrNull() ?: return null
    return if (value > 1.0) value / 100.0 else value
}

private fun formatPercent(value: Double?): String {
    return if (value == null) "n/a" else String.format("%.0f%%", value * 100.0)
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



