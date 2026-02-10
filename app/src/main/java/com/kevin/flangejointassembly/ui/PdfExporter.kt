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

    job.flangeForms.forEachIndexed { index, form ->
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, document.pageCount + 1).create()
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

        var y = MARGIN + 18
        canvas.drawText("FLANGE TORQUE REPORT", MARGIN.toFloat(), y.toFloat(), titlePaint)
        y += 18
        canvas.drawText("Job: ${job.number}", MARGIN.toFloat(), y.toFloat(), labelPaint)
        canvas.drawText("Location: ${job.location}", (MARGIN + 200).toFloat(), y.toFloat(), labelPaint)
        canvas.drawText("Date: ${formatDate(job.dateMillis)}", (MARGIN + 420).toFloat(), y.toFloat(), labelPaint)
        y += 20

        val leftX = MARGIN
        val rightX = PAGE_WIDTH / 2 + 10
        val columnGap = 12
        val columnWidth = PAGE_WIDTH / 2 - MARGIN - columnGap

        fun drawField(x: Int, label: String, value: String, width: Int): Int {
            canvas.drawText(label, x.toFloat(), y.toFloat(), labelPaint)
            val text = if (value.isBlank()) "-" else value
            canvas.drawText(text, (x + 130).toFloat(), y.toFloat(), valuePaint)
            return 14
        }

        fun drawFieldRight(label: String, value: String): Int {
            canvas.drawText(label, rightX.toFloat(), y.toFloat(), labelPaint)
            val text = if (value.isBlank()) "-" else value
            canvas.drawText(text, (rightX + 150).toFloat(), y.toFloat(), valuePaint)
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
            "Rounded Temp" to form.roundedTempF,
            "Torque Method" to form.torqueMethod,
            "Target F" to form.targetBoltLoadF,
            "Yield %" to form.pctYieldTarget,
            "TPI" to form.tpiUsed,
            "As" to form.asUsed,
            "Strength" to form.strengthKsiUsed,
            "K Used" to form.kUsed,
            "Calc Torque" to form.calculatedTargetTorque,
            "Specified" to form.specifiedTargetTorque,
            "Pass1" to if (form.pass1Confirmed) "Yes ${form.pass1Initials}" else form.pass1Initials,
            "Pass2" to if (form.pass2Confirmed) "Yes ${form.pass2Initials}" else form.pass2Initials,
            "Pass3" to if (form.pass3Confirmed) "Yes ${form.pass3Initials}" else form.pass3Initials,
            "Pass4" to if (form.pass4Confirmed) "Yes ${form.pass4Initials}" else form.pass4Initials
        )

        val rows = max(leftFields.size, rightFields.size)
        for (i in 0 until rows) {
            if (i < leftFields.size) {
                y += drawField(leftX, leftFields[i].first, leftFields[i].second, columnWidth)
            }
            if (i < rightFields.size) {
                drawFieldRight(rightFields[i].first, rightFields[i].second)
            }
        }

        y += 10
        canvas.drawText("Contractor Representative", leftX.toFloat(), y.toFloat(), headerPaint)
        y += 14
        drawSignatureBlock(canvas, leftX, y, "Print", form.contractorPrintName, linePaint, valuePaint)
        y += 18
        drawSignatureLine(context, canvas, leftX, y, "Sign", form.contractorSignUri, linePaint)
        y += 18
        drawSignatureBlock(canvas, leftX, y, "Date", if (form.contractorDateMillis > 0) formatDate(form.contractorDateMillis) else "", linePaint, valuePaint)

        y += 24
        canvas.drawText("Facility Representative", leftX.toFloat(), y.toFloat(), headerPaint)
        y += 14
        drawSignatureBlock(canvas, leftX, y, "Print", form.facilityPrintName, linePaint, valuePaint)
        y += 18
        drawSignatureLine(context, canvas, leftX, y, "Sign", form.facilitySignUri, linePaint)
        y += 18
        drawSignatureBlock(canvas, leftX, y, "Date", if (form.facilityDateMillis > 0) formatDate(form.facilityDateMillis) else "", linePaint, valuePaint)

        document.finishPage(page)

        if (form.photoUris.isNotEmpty()) {
            val photoPageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, document.pageCount + 1).create()
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

private fun drawSignatureLine(
    context: Context,
    canvas: android.graphics.Canvas,
    x: Int,
    y: Int,
    label: String,
    signatureUri: String,
    linePaint: Paint
) {
    val labelPaint = Paint().apply {
        color = Color.BLACK
        textSize = 10f
    }
    canvas.drawText("$label:", x.toFloat(), y.toFloat(), labelPaint)
    if (signatureUri.isBlank()) {
        canvas.drawLine((x + 40).toFloat(), (y + 2).toFloat(), (x + 220).toFloat(), (y + 2).toFloat(), linePaint)
    } else {
        val bitmap = loadBitmap(context, signatureUri)
        if (bitmap != null) {
            val scaled = scaleBitmapToFit(bitmap, 180, 40)
            canvas.drawBitmap(scaled, (x + 40).toFloat(), (y - 30).toFloat(), null)
        } else {
            canvas.drawLine((x + 40).toFloat(), (y + 2).toFloat(), (x + 220).toFloat(), (y + 2).toFloat(), linePaint)
        }
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
