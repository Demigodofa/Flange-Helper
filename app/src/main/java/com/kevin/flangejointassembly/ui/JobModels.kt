package com.kevin.flangejointassembly.ui

data class JobItem(
    val id: String,
    val number: String,
    val location: String,
    val dateMillis: Long,
    val flangeForms: List<FlangeFormItem> = emptyList()
)

data class FlangeFormItem(
    val id: String,
    val jobId: String,
    val dateMillis: Long,
    val description: String,
    val serviceType: String,
    val gasketType: String,
    val wrenchSerials: String,
    val wrenchCalDateMillis: Long,
    val torqueDry: Boolean,
    val torqueWet: Boolean,
    val lubricantType: String,
    val flangeClass: String,
    val pipeSize: String,
    val customInnerDiameter: String,
    val customOuterDiameter: String,
    val customThickness: String,
    val flangeFace: String,
    val boltHoles: String,
    val flangeFaceCondition: String,
    val flangeParallel: String,
    val fastenerType: String,
    val fastenerSpec: String,
    val fastenerLength: String,
    val fastenerDiameter: String,
    val nutSpec: String,
    val calculatedTargetTorque: String,
    val specifiedTargetTorque: String,
    val pass1Confirmed: Boolean,
    val pass1Initials: String,
    val pass2Confirmed: Boolean,
    val pass2Initials: String,
    val pass3Confirmed: Boolean,
    val pass3Initials: String,
    val pass4Confirmed: Boolean,
    val pass4Initials: String
)
