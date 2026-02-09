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
    val flangeClass: String,
    val pipeSize: String,
    val customInnerDiameter: String,
    val customOuterDiameter: String,
    val customThickness: String,
    val flangeFace: String,
    val boltHoles: String
)
