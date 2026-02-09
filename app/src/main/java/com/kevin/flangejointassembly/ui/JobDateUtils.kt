package com.kevin.flangejointassembly.ui

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

fun todayMillis(): Long {
    val today = LocalDate.now()
    return today.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun formatDate(millis: Long): String {
    val date = Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
    return date.format(dateFormatter)
}
