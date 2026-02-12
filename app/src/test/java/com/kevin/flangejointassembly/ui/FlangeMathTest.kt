package com.kevin.flangejointassembly.ui

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.math.abs

class FlangeMathTest {
    @Test
    fun boltSequenceMatchesReferenceData() {
        val sequenceByCount = loadSequenceLookup()
        for ((count, expected) in sequenceByCount) {
            val actual = FlangeMath.generateBoltSequence(count)
            assertEquals("Sequence mismatch for bolt count $count", expected, actual)
        }
    }

    @Test
    fun tensileStressAreaMatchesLookupWithinTolerance() {
        val root = loadRoot()
        val fasteners = root.getJSONObject("fasteners")
        val tpiLookup = parseSeriesLookup(fasteners.getJSONObject("tpi_lookup"))
        val asLookup = parseSeriesLookup(fasteners.getJSONObject("tensileStressArea_As_in2_lookup"))

        var checked = 0
        tpiLookup.forEach { (series, byDia) ->
            val asByDia = asLookup[series].orEmpty()
            byDia.forEach { (diameterKey, tpi) ->
                val asValue = asByDia[diameterKey]
                if (tpi == null || asValue == null) return@forEach
                val dIn = FlangeMath.parseDiameterInches(diameterKey) ?: return@forEach
                val calculated = FlangeMath.calculateTensileStressArea(dIn, tpi)
                assertNotNull(calculated)
                val diff = abs(calculated!! - asValue)
                assertTrue(
                    "As mismatch for $series $diameterKey: expected $asValue, got $calculated",
                    diff < 0.02
                )
                checked += 1
            }
        }
        assertTrue("No As values checked; test data missing?", checked > 0)
    }

    @Test
    fun torqueScalesWithCoefficients() {
        val asIn2 = 0.606
        val syKsi = 105.0
        val pct = 0.5
        val f = FlangeMath.computeBoltLoad(asIn2, syKsi, pct)
        val dIn = 1.0
        val k1 = 0.15
        val k2 = 0.30
        val t1 = FlangeMath.computeTargetTorque(k1, dIn, f)
        val t2 = FlangeMath.computeTargetTorque(k2, dIn, f)
        assertEquals(t1 * 2.0, t2, 0.0001)

        val f2 = f * 1.5
        val t3 = FlangeMath.computeTargetTorque(k1, dIn, f2)
        assertEquals(t1 * 1.5, t3, 0.0001)
    }

    @Test
    fun allDropdownCombinationsCompute() {
        val root = loadRoot()
        val fasteners = root.getJSONObject("fasteners")
        val diameterOptions = fasteners.getJSONArray("diameterOptions_in")
        val tpiLookup = parseSeriesLookup(fasteners.getJSONObject("tpi_lookup"))
        val asLookup = parseSeriesLookup(fasteners.getJSONObject("tensileStressArea_As_in2_lookup"))
        val boltGrades = fasteners.getJSONObject("boltGrades")
        val strengthLookup = boltGrades.getJSONObject("strength_Sy_Su_min_ksi")
        val gradeOptions = boltGrades.getJSONArray("options")

        var combinations = 0
        for (i in 0 until diameterOptions.length()) {
            val diameterKey = diameterOptions.getString(i)
            val diameterIn = FlangeMath.parseDiameterInches(diameterKey) ?: continue
            for (g in 0 until gradeOptions.length()) {
                val gradeKey = gradeOptions.getString(g)
                val sy = lookupSy(strengthLookup, gradeKey, diameterIn) ?: continue
                tpiLookup.forEach { (series, byDia) ->
                    val tpi = byDia[diameterKey] ?: return@forEach
                    val asValue = asLookup[series]?.get(diameterKey)
                        ?: FlangeMath.calculateTensileStressArea(diameterIn, tpi)
                        ?: return@forEach
                    val f = FlangeMath.computeBoltLoad(asValue, sy, 0.5)
                    val torque = FlangeMath.computeTargetTorque(0.2, diameterIn, f)
                    assertTrue("Torque not finite for $gradeKey $diameterKey $series", torque.isFinite())
                    combinations += 1
                }
            }
        }
        assertTrue("No combinations were computed; data may be missing.", combinations > 0)
    }

    private fun loadRoot(): JSONObject {
        val candidates = listOf(
            File("app/src/main/assets/flange_reference.json"),
            File("src/main/assets/flange_reference.json")
        )
        val file = candidates.firstOrNull { it.exists() }
            ?: throw IllegalArgumentException(
                "Missing flange_reference.json. Checked: " +
                    candidates.joinToString { it.absolutePath }
            )
        val json = file.readText()
        return JSONObject(json).getJSONObject("flange_helper_reference_data")
    }

    private fun loadSequenceLookup(): Map<Int, List<Int>> {
        val root = loadRoot()
        val tightening = root.getJSONObject("tightening")
        val sequences = tightening.getJSONObject("sequenceLookup").getJSONObject("sequenceByBoltCount")
        val out = mutableMapOf<Int, List<Int>>()
        for (key in sequences.keys()) {
            val count = key.toIntOrNull() ?: continue
            val array = sequences.getJSONArray(key)
            val list = mutableListOf<Int>()
            for (i in 0 until array.length()) {
                list.add(array.getInt(i))
            }
            out[count] = list
        }
        return out
    }

    private fun parseSeriesLookup(seriesObj: JSONObject): Map<String, Map<String, Double?>> {
        val result = mutableMapOf<String, Map<String, Double?>>()
        for (seriesKey in seriesObj.keys()) {
            val seriesMap = mutableMapOf<String, Double?>()
            val entries = seriesObj.getJSONObject(seriesKey)
            for (diaKey in entries.keys()) {
                seriesMap[diaKey] = anyToDouble(entries.opt(diaKey))
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

    private fun lookupSy(strengthLookup: JSONObject, gradeKey: String, diameterIn: Double): Double? {
        val ranges = strengthLookup.optJSONArray(gradeKey) ?: return null
        for (i in 0 until ranges.length()) {
            val obj = ranges.getJSONObject(i)
            val min = obj.optDouble("diaMin_in", 0.0)
            val max = obj.optDouble("diaMax_in", Double.MAX_VALUE)
            if (diameterIn >= min && diameterIn <= max) {
                return anyToDouble(obj.opt("Sy"))
            }
        }
        return null
    }
}
