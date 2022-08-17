package tororo1066.tororopluginapi.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

fun Date.toCalender(): Calendar {
    return Calendar.Builder().setInstant(this).build()
}