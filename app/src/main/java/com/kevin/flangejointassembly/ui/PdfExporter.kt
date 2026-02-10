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
import kotlin.math.max
import kotlin.math.min

private const val PAGE_WIDTH = 612
private const val PAGE_HEIGHT = 792
private const val MARGIN = 36

fun exportJobToPdf(context: Context, job: JobItem): Uri? {
    if (job.flangeForms.isEmpty()) return null

    val exportDir = File(context.filesDir, "flange_helper/exports")
    if (!exportDir.exists()) {
        exportDir.mkdirs()
    }
    val safeJobNumber = job.number.ifBlank { "job" }.replace("[^A-Za-z0-9_-]".toRegex(), "_")
    val fileName = "Job_${safeJobNumber}_${formatDateForFile(job.dateMillis)}.pdf"
    val file = File(exportDir, fileName)

    val document = PdfDocument()

    var pageNumber = 1
    job.flangeForms.forEachIndexed { index, form ->
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

        fun drawField(x: Int, label: String, value: String, width: Int): Int {
            canvas.drawText(label, x.toFloat(), y.toFloat(), labelPaint)
            val text = if (value.isBlank()) "-" else value
            val fitted = fitText(text, valuePaint, width)
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
            "Fastener Length" to form.fastenerLength,
            "Fastener Dia" to form.fastenerDiameter,
            "Thread Series" to form.threadSeries,
            "Nut Spec" to form.nutSpec
        )

        val rightFields = listOf(
            "Wrench S/N" to form.wrenchSerials,
            "Wrench Cal" to if (form.wrenchCalDateMillis > 0) formatDate(form.wrenchCalDateMillis) else "",
            "Torque Dry" to if (form.torqueDry) "Yes" else "",
            "Torque Wet" to if (form.torqueWet) "Yes" else "",
            "Lube" to form.lubricantType,
            "Work Temp" to form.workingTempF,
            "Torque Method" to form.torqueMethod,
            "Target Bolt Load F" to form.targetBoltLoadF,
            "Yield %" to form.pctYieldTarget,
            "TPI" to form.tpiUsed,
            "As" to form.asUsed,
            "Strength" to form.strengthKsiUsed,
            "K Used" to form.kUsed,
            "Calc Torque" to form.calculatedTargetTorque,
            "Specified" to form.specifiedTargetTorque,
            "Pass1" to formatPassLine(form, 1),
            "Pass2" to formatPassLine(form, 2),
            "Pass3" to formatPassLine(form, 3),
            "Pass4" to formatPassLine(form, 4)
        )

        val dataTop = y - 6
        val rows = max(leftFields.size, rightFields.size)
        for (i in 0 until rows) {
            if (i < leftFields.size) {
                y += drawField(leftX, leftFields[i].first, leftFields[i].second, columnWidth)
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

        y += 12
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
            val boltNote = "Starting at the 12 o'clock position and moving clockwise, " +
                "the bolt holes were marked 1, 2, 3, 4 ... $boltCount. " +
                "Tightening order: 1, 2, 3, 4 ..."
            canvas.drawText(fitText(boltNote, labelPaint, PAGE_WIDTH - 2 * MARGIN), leftX.toFloat(), y.toFloat(), labelPaint)
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
                    val scaled = scaleBitmapToFit(bitmap, cellWidth - 16, cellHeight - 16)
                    val destLeft = left + (cellWidth - scaled.width) / 2
                    val destTop = top + (cellHeight - scaled.height) / 2
                    photoCanvas.drawBitmap(scaled, destLeft.toFloat(), destTop.toFloat(), null)
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
        context.contentResolver.openInputStream(uri)?.use { stream ->
            BitmapFactory.decodeStream(stream)
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
            val scaled = scaleBitmapToFit(bitmap, boxWidth - 6, boxHeight - 6)
            val left = boxLeft + (boxWidth - scaled.width) / 2
            val top = boxTop + (boxHeight - scaled.height) / 2
            canvas.drawBitmap(scaled, left.toFloat(), top.toFloat(), null)
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
    val range = if (pass <= 2) {
        String.format("%.0f-%.0f ft-lb", low, high)
    } else {
        String.format("%.0f ft-lb", high)
    }
    val initials = when (pass) {
        1 -> form.pass1Initials
        2 -> form.pass2Initials
        3 -> form.pass3Initials
        else -> form.pass4Initials
    }
    return if (initials.isBlank()) range else "$range ($initials)"
}

private fun scaleBitmapToFit(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
    val ratio = min(maxWidth.toFloat() / bitmap.width.toFloat(), maxHeight.toFloat() / bitmap.height.toFloat())
    val newWidth = max(1, (bitmap.width * ratio).toInt())
    val newHeight = max(1, (bitmap.height * ratio).toInt())
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}

private fun formatDateForFile(millis: Long): String {
    val date = java.time.Instant.ofEpochMilli(millis)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalDate()
    return date.format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE)
}
