package com.kevin.flangejointassembly.ui

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

private const val PAGE_WIDTH = 612
private const val PAGE_HEIGHT = 792
private const val MARGIN = 36
private const val PDF_PHOTO_MAX_EDGE_PX = 1600

enum class PdfExportMode {
    EMAIL_FRIENDLY,
    FULL_RES
}

fun exportJobToPdf(
    context: Context,
    job: JobItem,
    mode: PdfExportMode = PdfExportMode.EMAIL_FRIENDLY
): Uri? {
    if (job.flangeForms.isEmpty()) return null

    val exportDir = File(context.filesDir, "flange_helper/exports")
    if (!exportDir.exists()) {
        exportDir.mkdirs()
    }
    val safeJobNumber = job.number.ifBlank { "job" }.replace("[^A-Za-z0-9_-]".toRegex(), "_")
    val fileName = "Job_${safeJobNumber}_${formatDateForFile(job.dateMillis)}.pdf"
    val file = File(exportDir, fileName)

    val document = PdfDocument()
    val nutPairingConfig = NutPairingConfig.load(context)

    var pageNumber = 1
    job.flangeForms.forEachIndexed { index, form ->
        val boltSpecKey = mapBoltSpecKey(form.fastenerSpec, form.fastenerClass)
        val nutKey = mapNutKey(form.nutSpec)
        val nutPairingEvaluation = NutPairingConfig.evaluate(nutPairingConfig, boltSpecKey, nutKey)
        val nutMismatchNote = if (!nutPairingEvaluation?.warnings.isNullOrEmpty()) {
            "Selected nut is not the commonly paired type for this stud material/grade. Proceed only if facility spec allows."
        } else {
            null
        }

        val diameterIn = FlangeMath.parseDiameterInches(form.fastenerDiameter)
        val asIn2 = form.asUsed.toDoubleOrNull()
        val strengthKsi = form.strengthKsiUsed.toDoubleOrNull()
        val pctYield = parsePercentValue(form.pctYieldTarget)
        val targetBoltStressKsi = if (strengthKsi != null && pctYield != null) strengthKsi * pctYield else null
        val calculatedBoltLoadF = if (asIn2 != null && targetBoltStressKsi != null) {
            targetBoltStressKsi * 1000.0 * asIn2
        } else {
            null
        }
        val targetBoltLoadF = form.targetBoltLoadF.toDoubleOrNull() ?: calculatedBoltLoadF
        val kUsed = form.kUsed.toDoubleOrNull()
        val calculatedTorque = if (targetBoltLoadF != null && diameterIn != null && kUsed != null) {
            (kUsed * diameterIn * targetBoltLoadF) / 12.0
        } else {
            null
        }

        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber++).create()
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            isFakeBoldText = true
        }
        val labelPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
        }
        val valuePaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            isFakeBoldText = true
        }
        val linePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }

        var y = MARGIN + 22
        val titleText = "FLANGE TORQUE REPORT"
        val titleWidth = titlePaint.measureText(titleText)
        val titleX = ((PAGE_WIDTH - titleWidth) / 2f).coerceAtLeast(MARGIN.toFloat())
        canvas.drawText(titleText, titleX, y.toFloat(), titlePaint)
        canvas.drawLine(titleX, y + 4f, titleX + titleWidth, y + 4f, linePaint)
        y += 26
        canvas.drawText("Job: ${job.number}", MARGIN.toFloat(), y.toFloat(), labelPaint)
        canvas.drawText("Location: ${job.location}", (MARGIN + 200).toFloat(), y.toFloat(), labelPaint)
        canvas.drawText("Date: ${formatDate(job.dateMillis)}", (MARGIN + 420).toFloat(), y.toFloat(), labelPaint)
        y += 26

        val leftX = MARGIN
        val rightX = PAGE_WIDTH / 2 + 10
        val columnGap = 12
        val columnWidth = PAGE_WIDTH / 2 - MARGIN - columnGap
        val valueOffset = 130
        val valueWidth = columnWidth - valueOffset
        val rightValueOffset = 150
        val rightValueWidth = PAGE_WIDTH - rightX - MARGIN - rightValueOffset

        fun drawField(x: Int, label: String, value: String, maxValueWidth: Int): Int {
            canvas.drawText(label, x.toFloat(), y.toFloat(), labelPaint)
            val text = if (value.isBlank()) "-" else value
            val fitted = fitText(text, valuePaint, maxValueWidth)
            canvas.drawText(fitted, (x + valueOffset).toFloat(), y.toFloat(), valuePaint)
            return 14
        }

        fun drawFieldRight(label: String, value: String): Int {
            canvas.drawText(label, rightX.toFloat(), y.toFloat(), labelPaint)
            val text = if (value.isBlank()) "-" else value
            val fitted = fitText(text, valuePaint, rightValueWidth)
            canvas.drawText(fitted, (rightX + rightValueOffset).toFloat(), y.toFloat(), valuePaint)
            return 14
        }

        val leftFields = listOf(
            "Form #" to (index + 1).toString(),
            "Flange Desc" to form.description,
            "Service Type" to form.serviceType,
            "Gasket Type" to form.gasketType,
            "Flange Class" to form.flangeClass,
            "Pipe Size" to form.pipeSize,
            "Custom I.D." to form.customInnerDiameter,
            "Custom O.D." to form.customOuterDiameter,
            "Thickness" to form.customThickness,
            "Flange Face" to form.flangeFace,
            "Bolt Holes" to form.boltHoles,
            "Face Condition" to form.flangeFaceCondition,
            "Parallel" to form.flangeParallel,
            "Fastener Type" to form.fastenerType,
            "Fastener Spec" to form.fastenerSpec,
            "Fastener Class" to form.fastenerClass,
            "Fastener Length" to formatInches(form.fastenerLength),
            "Fastener Dia" to formatInches(form.fastenerDiameter),
            "Thread Series" to form.threadSeries,
            "Nut Spec" to form.nutSpec,
            "Washer Used" to if (form.washerUsed) "Yes" else "No"
        )

        val rightFields = listOf(
            "Wrench S/N" to form.wrenchSerials,
            "Wrench Cal" to if (form.wrenchCalDateMillis > 0) formatDate(form.wrenchCalDateMillis) else "",
            "Lubricated" to if (form.torqueWet) "Yes" else "No",
            "Lubricant" to form.lubricantType,
            "Method" to formatTorqueMethod(form.torqueMethod),
            "Target Bolt Stress" to formatKsi(targetBoltStressKsi?.toString()),
            "Yield %" to formatPercentLabel(pctYield),
            "TPI" to formatTpi(form.tpiUsed),
            "As" to formatArea(form.asUsed),
            "S Value" to formatKsi(form.strengthKsiUsed),
            "Target Bolt Load F" to formatForce(targetBoltLoadF?.toString()),
            "Nut factor (K)" to formatK(form.kUsed),
            "Calc Torque" to formatTorque(form.calculatedTargetTorque.ifBlank { calculatedTorque?.let { String.format(Locale.US, "%.0f", it) } ?: "" }),
            "Specified" to formatTorque(form.specifiedTargetTorque),
            "Nut Override Ack" to if (form.nutOverrideAcknowledged) "Yes" else "No",
            "Pass1" to formatPassLine(form, 1),
            "Pass2" to formatPassLine(form, 2),
            "Pass3" to formatPassLine(form, 3),
            "Pass4" to formatPassLine(form, 4)
        )

        y += 8
        val dataTop = y - 8
        val rows = max(leftFields.size, rightFields.size)
        for (i in 0 until rows) {
            if (i < leftFields.size) {
                y += drawField(leftX, leftFields[i].first, leftFields[i].second, valueWidth)
            }
            if (i < rightFields.size) {
                drawFieldRight(rightFields[i].first, rightFields[i].second)
            }
        }
        val dataBottom = y + 4
        val leftBox = Rect(leftX - 6, dataTop, leftX + columnWidth + 6, dataBottom)
        val rightBox = Rect(rightX - 6, dataTop, PAGE_WIDTH - MARGIN + 6, dataBottom)
        canvas.drawRect(leftBox, linePaint)
        canvas.drawRect(rightBox, linePaint)

        y += 18
        if (!nutMismatchNote.isNullOrBlank()) {
            val notePaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                isFakeBoldText = true
            }
            val lines = wrapText("Nut Warning: $nutMismatchNote", notePaint, PAGE_WIDTH - (MARGIN * 2))
            lines.forEach { line ->
                y += 12
                canvas.drawText(line, MARGIN.toFloat(), y.toFloat(), notePaint)
            }
            y += 8
        } else {
            y += 4
        }
        y = drawSignatureSection(
            context = context,
            canvas = canvas,
            x = leftX,
            y = y,
            header = "Contractor Representative",
            printName = form.contractorPrintName,
            signUri = form.contractorSignUri,
            dateMillis = form.contractorDateMillis,
            headerPaint = headerPaint,
            labelPaint = labelPaint,
            valuePaint = valuePaint,
            linePaint = linePaint
        )

        y += 16
        y = drawSignatureSection(
            context = context,
            canvas = canvas,
            x = leftX,
            y = y,
            header = "Facility Representative",
            printName = form.facilityPrintName,
            signUri = form.facilitySignUri,
            dateMillis = form.facilityDateMillis,
            headerPaint = headerPaint,
            labelPaint = labelPaint,
            valuePaint = valuePaint,
            linePaint = linePaint
        )

        y += 18
        val boltCount = form.boltHoles.toIntOrNull()
        if (boltCount != null) {
            val markingOrder = FlangeMath.generateBoltSequence(boltCount).joinToString(", ")
            val boltNote = "Starting at the 12 o'clock position and moving clockwise, " +
                "mark each bolt in this order: $markingOrder ... " +
                "Tightening order: sequential 1, 2, 3, 4 ..."
            val lines = wrapText(boltNote, labelPaint, PAGE_WIDTH - 2 * MARGIN)
            lines.forEach { line ->
                canvas.drawText(line, leftX.toFloat(), y.toFloat(), labelPaint)
                y += 12
            }
        }

        document.finishPage(page)

        if (form.photoUris.isNotEmpty()) {
            val photoPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageNumber++).create()
            val photoPage = document.startPage(photoPageInfo)
            val photoCanvas = photoPage.canvas

            val photoTitlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 14f
                isFakeBoldText = true
            }
            photoCanvas.drawText("Flange Photos", MARGIN.toFloat(), (MARGIN + 14).toFloat(), photoTitlePaint)

            val gridTop = MARGIN + 30
            val gridWidth = PAGE_WIDTH - 2 * MARGIN
            val gridHeight = PAGE_HEIGHT - gridTop - MARGIN
            val cellWidth = gridWidth / 2
            val cellHeight = gridHeight / 2

            form.photoUris.take(4).forEachIndexed { photoIndex, uriString ->
                val col = photoIndex % 2
                val row = photoIndex / 2
                val left = MARGIN + col * cellWidth
                val top = gridTop + row * cellHeight
                val rect = Rect(left, top, left + cellWidth, top + cellHeight)
                val borderPaint = Paint().apply {
                    color = Color.GRAY
                    strokeWidth = 1f
                    style = Paint.Style.STROKE
                }
                photoCanvas.drawRect(rect, borderPaint)

                val bitmap = loadBitmap(context, uriString)
                if (bitmap != null) {
                    val inset = 8
                    val scaled = if (mode == PdfExportMode.EMAIL_FRIENDLY) {
                        scaleBitmapMaxEdge(bitmap, PDF_PHOTO_MAX_EDGE_PX)
                    } else {
                        bitmap
                    }
                    val destRect = Rect(
                        left + inset,
                        top + inset,
                        left + cellWidth - inset,
                        top + cellHeight - inset
                    )
                    photoCanvas.drawBitmap(scaled, null, destRect, null)
                }
            }

            document.finishPage(photoPage)
        }
    }

    FileOutputStream(file).use { output ->
        document.writeTo(output)
    }
    document.close()

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    return uri
}

private fun drawSignatureBlock(
    canvas: android.graphics.Canvas,
    x: Int,
    y: Int,
    label: String,
    value: String,
    linePaint: Paint,
    valuePaint: Paint
) {
    val labelPaint = Paint().apply {
        color = Color.BLACK
        textSize = 10f
    }
    canvas.drawText("$label:", x.toFloat(), y.toFloat(), labelPaint)
    if (value.isBlank()) {
        canvas.drawLine((x + 40).toFloat(), (y + 2).toFloat(), (x + 220).toFloat(), (y + 2).toFloat(), linePaint)
    } else {
        canvas.drawText(value, (x + 50).toFloat(), y.toFloat(), valuePaint)
    }
}

private fun loadBitmap(context: Context, uriString: String): Bitmap? {
    return runCatching {
        val uri = Uri.parse(uriString)
        val options = BitmapFactory.Options().apply {
            inScaled = false
        }
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream, null, options)
        }
    }.getOrNull()
}

private fun drawSignatureSection(
    context: Context,
    canvas: android.graphics.Canvas,
    x: Int,
    y: Int,
    header: String,
    printName: String,
    signUri: String,
    dateMillis: Long,
    headerPaint: Paint,
    labelPaint: Paint,
    valuePaint: Paint,
    linePaint: Paint
): Int {
    var currentY = y
    canvas.drawText(header, x.toFloat(), currentY.toFloat(), headerPaint)
    currentY += 14

    drawSignatureBlock(canvas, x, currentY, "Print", printName, linePaint, valuePaint)
    currentY += 18

    currentY = drawSignatureBox(context, canvas, x, currentY, "Sign", signUri, linePaint, labelPaint)
    currentY += 8

    val dateText = if (dateMillis > 0) formatDate(dateMillis) else ""
    drawSignatureBlock(canvas, x, currentY, "Date", dateText, linePaint, valuePaint)
    currentY += 18

    return currentY
}

private fun drawSignatureBox(
    context: Context,
    canvas: android.graphics.Canvas,
    x: Int,
    y: Int,
    label: String,
    signatureUri: String,
    linePaint: Paint,
    labelPaint: Paint
): Int {
    val boxLeft = x + 40
    val boxTop = y - 12
    val boxWidth = 220
    val boxHeight = 50
    canvas.drawText("$label:", x.toFloat(), y.toFloat(), labelPaint)
    canvas.drawRect(
        Rect(boxLeft, boxTop, boxLeft + boxWidth, boxTop + boxHeight),
        linePaint
    )
    if (signatureUri.isNotBlank()) {
        val bitmap = loadBitmap(context, signatureUri)
        if (bitmap != null) {
            val destRect = Rect(
                boxLeft + 3,
                boxTop + 3,
                boxLeft + boxWidth - 3,
                boxTop + boxHeight - 3
            )
            canvas.drawBitmap(bitmap, null, destRect, null)
        }
    }
    return y + boxHeight
}

private fun fitText(text: String, paint: Paint, maxWidth: Int): String {
    if (paint.measureText(text) <= maxWidth) return text
    val ellipsis = "..."
    var end = text.length
    while (end > 0) {
        val candidate = text.substring(0, end).trimEnd() + ellipsis
        if (paint.measureText(candidate) <= maxWidth) return candidate
        end -= 1
    }
    return text.take(1) + ellipsis
}

private fun formatPassLine(form: FlangeFormItem, pass: Int): String {
    val target = form.specifiedTargetTorque.toDoubleOrNull()
        ?: form.calculatedTargetTorque.toDoubleOrNull()
        ?: return when (pass) {
            1 -> form.pass1Initials
            2 -> form.pass2Initials
            3 -> form.pass3Initials
            else -> form.pass4Initials
        }
    val (lowPct, highPct) = when (pass) {
        1 -> 0.20 to 0.30
        2 -> 0.50 to 0.70
        3 -> 1.0 to 1.0
        else -> 1.0 to 1.0
    }
    val low = target * lowPct
    val high = target * highPct
    val pctLabel = if (pass <= 2) {
        String.format(Locale.US, "%.0f-%.0f%%", lowPct * 100.0, highPct * 100.0)
    } else {
        "100%"
    }
    val range = if (pass <= 2) {
        String.format(Locale.US, "%s (%.0f-%.0f ft-lb)", pctLabel, low, high)
    } else {
        String.format(Locale.US, "%s (%.0f ft-lb)", pctLabel, high)
    }
    val initials = when (pass) {
        1 -> form.pass1Initials
        2 -> form.pass2Initials
        3 -> form.pass3Initials
        else -> form.pass4Initials
    }
    return if (initials.isBlank()) range else "$range $initials"
}

private fun formatTemp(value: String): String {
    val temp = value.toDoubleOrNull() ?: return ""
    return String.format(Locale.US, "%.0f F", temp)
}

private fun formatTpi(value: String): String {
    val tpi = value.toDoubleOrNull() ?: return value
    return String.format(Locale.US, "%.0f threads/in", tpi)
}

private fun formatArea(value: String): String {
    val area = value.toDoubleOrNull() ?: return value
    return String.format(Locale.US, "%.4f in^2", area)
}

private fun formatKsi(value: String?): String {
    val ksi = value?.toDoubleOrNull() ?: return ""
    return String.format(Locale.US, "%.1f ksi", ksi)
}

private fun formatForce(value: String?): String {
    val f = value?.toDoubleOrNull() ?: return ""
    return String.format(Locale.US, "%.0f lbf", f)
}

private fun formatTorque(value: String): String {
    val torque = value.toDoubleOrNull() ?: return ""
    return String.format(Locale.US, "%.0f ft-lb", torque)
}

private fun formatK(value: String): String {
    val k = value.toDoubleOrNull() ?: return value
    return String.format(Locale.US, "K=%.2f", k)
}

private fun formatPercentLabel(value: Double?): String {
    if (value == null) return ""
    return String.format(Locale.US, "%.0f%%", value * 100.0)
}

private fun formatTorqueMethod(value: String): String {
    return when (value) {
        "YIELD_PERCENT" -> "% of yield"
        "USER_INPUT" -> "Use F directly"
        "SPECIFIED_TORQUE" -> "Specified torque"
        else -> value
    }
}

private fun formatInches(value: String): String {
    if (value.isBlank()) return ""
    return "$value in"
}

private fun parsePercentValue(text: String): Double? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    val value = trimmed.toDoubleOrNull() ?: return null
    return if (value > 1.0) value / 100.0 else value
}

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

private fun wrapText(text: String, paint: Paint, maxWidth: Int): List<String> {
    val words = text.split(" ")
    val lines = mutableListOf<String>()
    var current = ""
    for (word in words) {
        val candidate = if (current.isBlank()) word else "$current $word"
        if (paint.measureText(candidate) <= maxWidth) {
            current = candidate
        } else {
            if (current.isNotBlank()) {
                lines.add(current)
            }
            current = word
        }
    }
    if (current.isNotBlank()) {
        lines.add(current)
    }
    return lines
}

private fun scaleBitmapMaxEdge(bitmap: Bitmap, maxEdge: Int): Bitmap {
    val edge = max(bitmap.width, bitmap.height)
    if (edge <= maxEdge) return bitmap
    val ratio = maxEdge.toFloat() / edge.toFloat()
    val newWidth = max(1, (bitmap.width * ratio).toInt())
    val newHeight = max(1, (bitmap.height * ratio).toInt())
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

private fun formatDateForFile(millis: Long): String {
    val formatter = SimpleDateFormat("yyyyMMdd", Locale.US)
    return formatter.format(Date(millis))
}
