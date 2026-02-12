package com.kevin.flangejointassembly.ui

object FlangeMath {
    data class AllowableResult(
        val s: Double,
        val usedTemp: Int
    )

    fun parseDiameterInches(value: String): Double? {
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

    fun normalizeDiameterKey(value: String): String {
        return value.replace(" (in.)", "").trim()
    }

    fun defaultThreadSeriesFor(diameterIn: Double?): String {
        if (diameterIn == null) return ""
        return if (diameterIn >= 1.0) "8UN" else "UNC"
    }

    fun calculateTensileStressArea(diameterIn: Double?, tpi: Double?): Double? {
        if (diameterIn == null || tpi == null || tpi == 0.0) return null
        val term = diameterIn - (0.9743 / tpi)
        return 0.7854 * term * term
    }

    fun lookupSy(data: ReferenceData.Data?, gradeKey: String, diameterIn: Double?): Double? {
        if (data == null || diameterIn == null) return null
        val ranges = data.strengthLookup[gradeKey] ?: return null
        return ranges.firstOrNull { diameterIn >= it.diaMin && diameterIn <= it.diaMax }?.sy
    }

    fun lookupAllowableStress(
        data: ReferenceData.Data?,
        gradeKey: String?,
        diameterIn: Double?,
        workingTempF: Int?
    ): AllowableResult? {
        if (data == null || gradeKey == null || diameterIn == null || workingTempF == null) return null
        val ranges = data.allowableStressLookup[gradeKey] ?: return null
        val range = ranges.firstOrNull { diameterIn >= it.diaMin && diameterIn <= it.diaMax } ?: return null
        val rounded = ((workingTempF + 49) / 50) * 50
        val exact = range.temps.firstOrNull { rounded >= it.tMin && rounded <= it.tMax && it.tMin == it.tMax }
            ?: range.temps.firstOrNull { rounded >= it.tMin && rounded <= it.tMax }
            ?: return null
        return AllowableResult(s = exact.s, usedTemp = rounded)
    }

    fun generateBoltSequence(boltCount: Int): List<Int> {
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

    fun computeBoltLoad(asIn2: Double, syKsi: Double, pctYield: Double): Double {
        return asIn2 * (syKsi * 1000.0) * pctYield
    }

    fun computeTargetTorque(k: Double, diameterIn: Double, boltLoadF: Double): Double {
        return (k * diameterIn * boltLoadF) / 12.0
    }

    fun computePassTorque(targetTorque: Double, pct: Double): Double {
        return targetTorque * pct
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

    private fun reverseBits(value: Int, bitCount: Int): Int {
        var v = value
        var result = 0
        repeat(bitCount) {
            result = (result shl 1) or (v and 1)
            v = v shr 1
        }
        return result
    }
}
