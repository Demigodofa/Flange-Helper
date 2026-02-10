package com.kevin.flangejointassembly.ui

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object JobStorage {
    private const val STORAGE_DIR = "flange_helper"
    private const val JOBS_FILE = "jobs.json"
    const val STORAGE_LIMIT_BYTES: Long = 750L * 1024L * 1024L

    fun loadJobs(context: Context): List<JobItem> {
        val file = File(storageRoot(context), JOBS_FILE)
        if (!file.exists()) return emptyList()
        return try {
            val content = file.readText()
            if (content.isBlank()) return emptyList()
            val array = JSONArray(content)
            buildList {
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val formsArray = obj.optJSONArray("forms") ?: JSONArray()
                    val forms = buildList {
                        for (j in 0 until formsArray.length()) {
                            val formObj = formsArray.getJSONObject(j)
                            add(
                                FlangeFormItem(
                                    id = formObj.optString("id"),
                                    jobId = formObj.optString("jobId"),
                                    dateMillis = formObj.optLong("dateMillis"),
                                    description = formObj.optString("description"),
                                    serviceType = formObj.optString("serviceType"),
                                    gasketType = formObj.optString("gasketType"),
                                    wrenchSerials = formObj.optString("wrenchSerials"),
                                    wrenchCalDateMillis = formObj.optLong("wrenchCalDateMillis"),
                                    torqueDry = formObj.optBoolean("torqueDry"),
                                    torqueWet = formObj.optBoolean("torqueWet"),
                                    lubricantType = formObj.optString("lubricantType"),
                                    flangeClass = formObj.optString("flangeClass"),
                                    pipeSize = formObj.optString("pipeSize"),
                                    customInnerDiameter = formObj.optString("customInnerDiameter"),
                                    customOuterDiameter = formObj.optString("customOuterDiameter"),
                                    customThickness = formObj.optString("customThickness"),
                                    flangeFace = formObj.optString("flangeFace"),
                                    boltHoles = formObj.optString("boltHoles"),
                                    flangeFaceCondition = formObj.optString("flangeFaceCondition"),
                                    flangeParallel = formObj.optString("flangeParallel"),
                                    fastenerType = formObj.optString("fastenerType"),
                                    fastenerSpec = formObj.optString("fastenerSpec"),
                                    fastenerClass = formObj.optString("fastenerClass"),
                                    fastenerLength = formObj.optString("fastenerLength"),
                                    fastenerDiameter = formObj.optString("fastenerDiameter"),
                                    threadSeries = formObj.optString("threadSeries"),
                                    nutSpec = formObj.optString("nutSpec"),
                                    workingTempF = formObj.optString("workingTempF"),
                                    roundedTempF = formObj.optString("roundedTempF"),
                                    torqueMethod = formObj.optString("torqueMethod"),
                                    targetBoltLoadF = formObj.optString("targetBoltLoadF"),
                                    pctYieldTarget = formObj.optString("pctYieldTarget"),
                                    tpiUsed = formObj.optString("tpiUsed"),
                                    asUsed = formObj.optString("asUsed"),
                                    strengthKsiUsed = formObj.optString("strengthKsiUsed"),
                                    kUsed = formObj.optString("kUsed"),
                                    calculatedTargetTorque = formObj.optString("calculatedTargetTorque"),
                                    specifiedTargetTorque = formObj.optString("specifiedTargetTorque"),
                                    pass1Confirmed = formObj.optBoolean("pass1Confirmed"),
                                    pass1Initials = formObj.optString("pass1Initials"),
                                    pass2Confirmed = formObj.optBoolean("pass2Confirmed"),
                                    pass2Initials = formObj.optString("pass2Initials"),
                                    pass3Confirmed = formObj.optBoolean("pass3Confirmed"),
                                    pass3Initials = formObj.optString("pass3Initials"),
                                    pass4Confirmed = formObj.optBoolean("pass4Confirmed"),
                                    pass4Initials = formObj.optString("pass4Initials"),
                                    photoUris = run {
                                        val photosArray = formObj.optJSONArray("photoUris") ?: JSONArray()
                                        buildList {
                                            for (k in 0 until photosArray.length()) {
                                                add(photosArray.optString(k))
                                            }
                                        }
                                    },
                                    contractorPrintName = formObj.optString("contractorPrintName"),
                                    contractorSignUri = formObj.optString("contractorSignUri"),
                                    contractorDateMillis = formObj.optLong("contractorDateMillis"),
                                    facilityPrintName = formObj.optString("facilityPrintName"),
                                    facilitySignUri = formObj.optString("facilitySignUri"),
                                    facilityDateMillis = formObj.optLong("facilityDateMillis")
                                )
                            )
                        }
                    }
                    add(
                        JobItem(
                            id = obj.optString("id"),
                            number = obj.optString("number"),
                            location = obj.optString("location"),
                            dateMillis = obj.optLong("dateMillis"),
                            flangeForms = forms
                        )
                    )
                }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveJobs(context: Context, jobs: List<JobItem>) {
        val root = storageRoot(context)
        if (!root.exists()) {
            root.mkdirs()
        }
        val array = JSONArray()
        jobs.forEach { job ->
            val obj = JSONObject()
            obj.put("id", job.id)
            obj.put("number", job.number)
            obj.put("location", job.location)
            obj.put("dateMillis", job.dateMillis)
            val formsArray = JSONArray()
            job.flangeForms.forEach { form ->
                val formObj = JSONObject()
                formObj.put("id", form.id)
                formObj.put("jobId", form.jobId)
                formObj.put("dateMillis", form.dateMillis)
                formObj.put("description", form.description)
                formObj.put("serviceType", form.serviceType)
                formObj.put("gasketType", form.gasketType)
                formObj.put("wrenchSerials", form.wrenchSerials)
                formObj.put("wrenchCalDateMillis", form.wrenchCalDateMillis)
                formObj.put("torqueDry", form.torqueDry)
                formObj.put("torqueWet", form.torqueWet)
                formObj.put("lubricantType", form.lubricantType)
                formObj.put("flangeClass", form.flangeClass)
                formObj.put("pipeSize", form.pipeSize)
                formObj.put("customInnerDiameter", form.customInnerDiameter)
                formObj.put("customOuterDiameter", form.customOuterDiameter)
                formObj.put("customThickness", form.customThickness)
                formObj.put("flangeFace", form.flangeFace)
                formObj.put("boltHoles", form.boltHoles)
                formObj.put("flangeFaceCondition", form.flangeFaceCondition)
                formObj.put("flangeParallel", form.flangeParallel)
                formObj.put("fastenerType", form.fastenerType)
                formObj.put("fastenerSpec", form.fastenerSpec)
                formObj.put("fastenerClass", form.fastenerClass)
                formObj.put("fastenerLength", form.fastenerLength)
                formObj.put("fastenerDiameter", form.fastenerDiameter)
                formObj.put("threadSeries", form.threadSeries)
                formObj.put("nutSpec", form.nutSpec)
                formObj.put("workingTempF", form.workingTempF)
                formObj.put("roundedTempF", form.roundedTempF)
                formObj.put("torqueMethod", form.torqueMethod)
                formObj.put("targetBoltLoadF", form.targetBoltLoadF)
                formObj.put("pctYieldTarget", form.pctYieldTarget)
                formObj.put("tpiUsed", form.tpiUsed)
                formObj.put("asUsed", form.asUsed)
                formObj.put("strengthKsiUsed", form.strengthKsiUsed)
                formObj.put("kUsed", form.kUsed)
                formObj.put("calculatedTargetTorque", form.calculatedTargetTorque)
                formObj.put("specifiedTargetTorque", form.specifiedTargetTorque)
                formObj.put("pass1Confirmed", form.pass1Confirmed)
                formObj.put("pass1Initials", form.pass1Initials)
                formObj.put("pass2Confirmed", form.pass2Confirmed)
                formObj.put("pass2Initials", form.pass2Initials)
                formObj.put("pass3Confirmed", form.pass3Confirmed)
                formObj.put("pass3Initials", form.pass3Initials)
                formObj.put("pass4Confirmed", form.pass4Confirmed)
                formObj.put("pass4Initials", form.pass4Initials)
                val photosArray = JSONArray()
                form.photoUris.forEach { uri -> photosArray.put(uri) }
                formObj.put("photoUris", photosArray)
                formObj.put("contractorPrintName", form.contractorPrintName)
                formObj.put("contractorSignUri", form.contractorSignUri)
                formObj.put("contractorDateMillis", form.contractorDateMillis)
                formObj.put("facilityPrintName", form.facilityPrintName)
                formObj.put("facilitySignUri", form.facilitySignUri)
                formObj.put("facilityDateMillis", form.facilityDateMillis)
                formsArray.put(formObj)
            }
            obj.put("forms", formsArray)
            array.put(obj)
        }
        val file = File(root, JOBS_FILE)
        file.writeText(array.toString())
    }

    fun calculateStorageBytes(context: Context): Long {
        val root = storageRoot(context)
        return calculateDirectorySize(root)
    }

    private fun storageRoot(context: Context): File {
        return File(context.filesDir, STORAGE_DIR)
    }

    private fun calculateDirectorySize(file: File): Long {
        if (!file.exists()) return 0L
        if (file.isFile) return file.length()
        var total = 0L
        file.listFiles()?.forEach { child ->
            total += calculateDirectorySize(child)
        }
        return total
    }
}
