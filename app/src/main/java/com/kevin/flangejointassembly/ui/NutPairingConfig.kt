package com.kevin.flangejointassembly.ui

import android.content.Context
import org.json.JSONObject

data class BoltSpecKey(
    val spec: String,
    val grade: String,
    val klass: String?
)

data class NutRecommendation(
    val nut: String,
    val label: String
)

data class WarningRule(
    val ifNutIs: String? = null,
    val ifNutNotInRecommended: Boolean = false,
    val ifBoltIsStainless: Boolean = false,
    val severity: String,
    val messageKey: String
)

data class PairingRule(
    val bolt: BoltSpecKey,
    val recommended: List<NutRecommendation>,
    val mismatchWarnings: List<WarningRule>,
    val specialWarnings: List<WarningRule>,
    val boltPropertyWarnings: List<WarningRule>
)

data class WarningMessage(
    val severity: String,
    val message: String
)

data class PairingEvaluation(
    val recommended: List<NutRecommendation>,
    val warnings: List<WarningMessage>,
    val requiresAck: Boolean
)

object NutPairingConfig {
    data class Config(
        val rules: List<PairingRule>,
        val messageCatalog: Map<String, String>
    )

    fun load(context: Context): Config? {
        return try {
            val json = context.assets.open("nut_pairing.json").bufferedReader().use { it.readText() }
            val root = JSONObject(json)
            val catalog = mutableMapOf<String, String>()
            val catalogObj = root.optJSONObject("messageCatalog")
            if (catalogObj != null) {
                for (key in catalogObj.keys()) {
                    catalog[key] = catalogObj.optString(key)
                }
            }
            val rules = mutableListOf<PairingRule>()
            val rulesArray = root.optJSONArray("pairingRules")
            if (rulesArray != null) {
                for (i in 0 until rulesArray.length()) {
                    val ruleObj = rulesArray.getJSONObject(i)
                    val boltObj = ruleObj.getJSONObject("bolt")
                    val bolt = BoltSpecKey(
                        spec = boltObj.optString("spec"),
                        grade = boltObj.optString("grade"),
                        klass = boltObj.opt("class") as? String
                    )
                    val recs = mutableListOf<NutRecommendation>()
                    val recArray = ruleObj.optJSONArray("recommendedNuts")
                    if (recArray != null) {
                        for (j in 0 until recArray.length()) {
                            val rec = recArray.getJSONObject(j)
                            recs.add(NutRecommendation(rec.optString("nut"), rec.optString("label")))
                        }
                    }
                    val mismatchWarnings = parseWarnings(ruleObj.optJSONArray("mismatchWarnings"))
                    val specialWarnings = parseWarnings(ruleObj.optJSONArray("specialWarnings"))
                    val boltWarnings = parseWarnings(ruleObj.optJSONArray("boltPropertyWarnings"))
                    rules.add(
                        PairingRule(
                            bolt = bolt,
                            recommended = recs,
                            mismatchWarnings = mismatchWarnings,
                            specialWarnings = specialWarnings,
                            boltPropertyWarnings = boltWarnings
                        )
                    )
                }
            }
            Config(rules = rules, messageCatalog = catalog)
        } catch (_: Exception) {
            null
        }
    }

    fun evaluate(
        config: Config?,
        bolt: BoltSpecKey?,
        nutKey: String?
    ): PairingEvaluation? {
        if (config == null || bolt == null) return null
        val rule = config.rules.firstOrNull {
            it.bolt.spec == bolt.spec &&
                it.bolt.grade == bolt.grade &&
                ((it.bolt.klass ?: "") == (bolt.klass ?: ""))
        } ?: return null

        val recommendedKeys = rule.recommended.map { it.nut }.toSet()
        val mismatch = nutKey != null && nutKey.isNotBlank() && !recommendedKeys.contains(nutKey)

        val warnings = mutableListOf<WarningMessage>()

        val isStainless = bolt.grade.uppercase().contains("B8")

        rule.boltPropertyWarnings.forEach { warn ->
            if (warn.ifBoltIsStainless && isStainless) {
                warnings.add(WarningMessage(warn.severity, resolveMessage(config, warn.messageKey)))
            }
        }

        rule.specialWarnings.forEach { warn ->
            if (warn.ifNutIs != null && warn.ifNutIs == nutKey) {
                warnings.add(WarningMessage(warn.severity, resolveMessage(config, warn.messageKey)))
            }
        }

        rule.mismatchWarnings.forEach { warn ->
            if (warn.ifNutNotInRecommended && mismatch) {
                warnings.add(WarningMessage(warn.severity, resolveMessage(config, warn.messageKey)))
            }
        }

        val requiresAck = warnings.any { it.severity.equals("high", true) } || mismatch

        return PairingEvaluation(
            recommended = rule.recommended,
            warnings = warnings,
            requiresAck = requiresAck
        )
    }

    private fun parseWarnings(array: org.json.JSONArray?): List<WarningRule> {
        if (array == null) return emptyList()
        val out = mutableListOf<WarningRule>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            out.add(
                WarningRule(
                    ifNutIs = obj.optString("ifNutIs").takeIf { it.isNotBlank() },
                    ifNutNotInRecommended = obj.optBoolean("ifNutNotInRecommended", false),
                    ifBoltIsStainless = obj.optBoolean("ifBoltIsStainless", false),
                    severity = obj.optString("severity"),
                    messageKey = obj.optString("messageKey")
                )
            )
        }
        return out
    }

    private fun resolveMessage(config: Config, messageKey: String): String {
        return config.messageCatalog[messageKey] ?: messageKey
    }
}
