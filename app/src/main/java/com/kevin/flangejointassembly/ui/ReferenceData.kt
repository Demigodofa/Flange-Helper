package com.kevin.flangejointassembly.ui

import android.content.Context
import org.json.JSONObject

object ReferenceData {
    data class TempRange(
        val tMin: Int,
        val tMax: Int,
        val s: Double
    )

    data class AllowableStressRange(
        val diaMin: Double,
        val diaMax: Double,
        val temps: List<TempRange>
    )

    data class StrengthRange(
        val diaMin: Double,
        val diaMax: Double,
        val sy: Double?,
        val su: Double?
    )

    data class Data(
        val tpiLookup: Map<String, Map<String, Double?>>,
        val asLookup: Map<String, Map<String, Double?>>,
        val strengthLookup: Map<String, List<StrengthRange>>,
        val allowableStressLookup: Map<String, List<AllowableStressRange>>,
        val boltSequenceLookup: Map<Int, List<Int>>,
        val boltNumberingDirection: String,
        val boltNumberingRule: String
    )

    fun load(context: Context): Data {
        return try {
            val json = context.assets.open("flange_reference.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json).getJSONObject("flange_helper_reference_data")
            val fasteners = root.getJSONObject("fasteners")

            val tpiLookup = parseSeriesLookup(fasteners.getJSONObject("tpi_lookup"))
            val asLookup = parseSeriesLookup(fasteners.getJSONObject("tensileStressArea_As_in2_lookup"))

            val boltGrades = fasteners.getJSONObject("boltGrades")

            val strengthLookup = mutableMapOf<String, List<StrengthRange>>()
            val grades = boltGrades.getJSONObject("strength_Sy_Su_min_ksi")
            for (gradeKey in grades.keys()) {
                val ranges = mutableListOf<StrengthRange>()
                val array = grades.getJSONArray(gradeKey)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    ranges.add(
                        StrengthRange(
                            diaMin = obj.optDouble("diaMin_in", 0.0),
                            diaMax = obj.optDouble("diaMax_in", Double.MAX_VALUE),
                            sy = anyToDouble(obj.opt("Sy")),
                            su = anyToDouble(obj.opt("Su"))
                        )
                    )
                }
                strengthLookup[gradeKey] = ranges
            }

            val allowableStressLookup = mutableMapOf<String, List<AllowableStressRange>>()
            if (boltGrades.has("allowableStress_S_ksi_atTemp")) {
                val allowable = boltGrades.getJSONObject("allowableStress_S_ksi_atTemp")
                for (gradeKey in allowable.keys()) {
                    val ranges = mutableListOf<AllowableStressRange>()
                    val array = allowable.getJSONArray(gradeKey)
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val temps = mutableListOf<TempRange>()
                        val tempsArray = obj.getJSONArray("temps")
                        for (j in 0 until tempsArray.length()) {
                            val tempObj = tempsArray.getJSONObject(j)
                            temps.add(
                                TempRange(
                                    tMin = tempObj.optInt("tMin"),
                                    tMax = tempObj.optInt("tMax"),
                                    s = tempObj.optDouble("S")
                                )
                            )
                        }
                        ranges.add(
                            AllowableStressRange(
                                diaMin = obj.optDouble("diaMin_in", 0.0),
                                diaMax = obj.optDouble("diaMax_in", Double.MAX_VALUE),
                                temps = temps
                            )
                        )
                    }
                    allowableStressLookup[gradeKey] = ranges
                }
            }

            var boltSequenceLookup: Map<Int, List<Int>> = emptyMap()
            var boltNumberingDirection = ""
            var boltNumberingRule = ""

            val tightening = root.optJSONObject("tightening")
            if (tightening != null) {
                val numbering = tightening.optJSONObject("boltNumbering")
                if (numbering != null) {
                    boltNumberingDirection = numbering.optString("direction")
                    boltNumberingRule = numbering.optString("rule")
                }

                val sequenceByCount = tightening
                    .optJSONObject("sequenceLookup")
                    ?.optJSONObject("sequenceByBoltCount")

                if (sequenceByCount != null) {
                    val sequences = mutableMapOf<Int, List<Int>>()
                    for (key in sequenceByCount.keys()) {
                        val count = key.toIntOrNull() ?: continue
                        val array = sequenceByCount.getJSONArray(key)
                        val list = mutableListOf<Int>()
                        for (i in 0 until array.length()) {
                            list.add(array.optInt(i))
                        }
                        sequences[count] = list
                    }
                    boltSequenceLookup = sequences
                }
            }

            Data(
                tpiLookup = tpiLookup,
                asLookup = asLookup,
                strengthLookup = strengthLookup,
                allowableStressLookup = allowableStressLookup,
                boltSequenceLookup = boltSequenceLookup,
                boltNumberingDirection = boltNumberingDirection,
                boltNumberingRule = boltNumberingRule
            )
        } catch (_: Exception) {
            Data(
                tpiLookup = emptyMap(),
                asLookup = emptyMap(),
                strengthLookup = emptyMap(),
                allowableStressLookup = emptyMap(),
                boltSequenceLookup = emptyMap(),
                boltNumberingDirection = "",
                boltNumberingRule = ""
            )
        }
    }

    private fun parseSeriesLookup(seriesObj: JSONObject): Map<String, Map<String, Double?>> {
        val result = mutableMapOf<String, Map<String, Double?>>()
        for (seriesKey in seriesObj.keys()) {
            val seriesMap = mutableMapOf<String, Double?>()
            val entries = seriesObj.getJSONObject(seriesKey)
            for (diaKey in entries.keys()) {
                val value = entries.opt(diaKey)
                seriesMap[diaKey] = anyToDouble(value)
            }
            result[seriesKey] = seriesMap
        }
        return result
    }

    private fun anyToDouble(value: Any?): Double? {
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            JSONObject.NULL, null -> null
            else -> null
        }
    }
}
