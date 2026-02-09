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
                    add(
                        JobItem(
                            id = obj.optString("id"),
                            number = obj.optString("number"),
                            location = obj.optString("location"),
                            dateMillis = obj.optLong("dateMillis")
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
