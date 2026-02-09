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
                                    flangeClass = formObj.optString("flangeClass"),
                                    pipeSize = formObj.optString("pipeSize"),
                                    customInnerDiameter = formObj.optString("customInnerDiameter"),
                                    customOuterDiameter = formObj.optString("customOuterDiameter"),
                                    customThickness = formObj.optString("customThickness"),
                                    flangeFace = formObj.optString("flangeFace"),
                                    boltHoles = formObj.optString("boltHoles")
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
                formObj.put("flangeClass", form.flangeClass)
                formObj.put("pipeSize", form.pipeSize)
                formObj.put("customInnerDiameter", form.customInnerDiameter)
                formObj.put("customOuterDiameter", form.customOuterDiameter)
                formObj.put("customThickness", form.customThickness)
                formObj.put("flangeFace", form.flangeFace)
                formObj.put("boltHoles", form.boltHoles)
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
