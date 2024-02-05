package com.smh.nxleave.utility

import com.google.firebase.Timestamp
import org.checkerframework.checker.units.qual.s
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date

val DATE_PATTERN_ONE = DateTimeFormatter.ofPattern("dd MMM yyyy")
val DATE_PATTERN_THREE = DateTimeFormatter.ofPattern("d MMM")
val DATE_TIME_PATTERN_ONE =  DateTimeFormatter.ofPattern("dd_MMM_yyyy_hh_mm_ss")

fun OffsetDateTime.toTimeStamp(): Timestamp {
    return Timestamp(Date.from(this.toInstant()))
}

fun Timestamp.toOffsetDateTime(): OffsetDateTime {
    val instant = this.toDate().toInstant()
    return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
}

fun getTodayStartTimeStamp(): Timestamp {
    val now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0)
    val instant = now.atZone(ZoneOffset.UTC).toInstant()
    val date = Date.from(instant)
    return Timestamp(date)
}

fun getCurrentMonthStartAndEndOffsetDate(): Pair<OffsetDateTime, OffsetDateTime> {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)

    val startDate = OffsetDateTime.of(year, month + 1, 1, 0, 0, 0, 0, ZoneOffset.UTC)
    val endDate = startDate.plusMonths(1).minusDays(1)
    return Pair(startDate, endDate)
}

