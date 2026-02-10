package com.kevin.flangejointassembly.ui

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private val dateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.US)

fun todayMillis(): Long {
    val cal = Calendar.getInstance()
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

fun formatDate(millis: Long): String {
    return dateFormatter.format(Date(millis))
}

fun normalizePickerMillis(selectedUtcMillis: Long): Long {
    val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utcCal.timeInMillis = selectedUtcMillis
    val year = utcCal.get(Calendar.YEAR)
    val month = utcCal.get(Calendar.MONTH)
    val day = utcCal.get(Calendar.DAY_OF_MONTH)

    val localCal = Calendar.getInstance()
    localCal.set(Calendar.YEAR, year)
    localCal.set(Calendar.MONTH, month)
    localCal.set(Calendar.DAY_OF_MONTH, day)
    localCal.set(Calendar.HOUR_OF_DAY, 0)
    localCal.set(Calendar.MINUTE, 0)
    localCal.set(Calendar.SECOND, 0)
    localCal.set(Calendar.MILLISECOND, 0)
    return localCal.timeInMillis
}
