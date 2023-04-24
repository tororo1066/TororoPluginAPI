package tororo1066.tororopluginapi.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

fun LocalDateTime.toCalender(): Calendar {
    return Calendar.Builder().setInstant(this.toDate()).build()
}

fun Date.toCalender(): Calendar {
    return Calendar.Builder().setInstant(this).build()
}

enum class DateType {
    SECOND,
    MINUTE,
    HOUR,
    DAY,
    MONTH,
    YEAR
}

fun Long.toJPNDateStr(longType: DateType, toDateType: DateType, includeDuration: Boolean, vararg replaceDate: DateType): String? {
    var second = 0L
    var minute = 0L
    var hours = 0L
    var days = 0L
    var months = 0L
    var years = 0L
    when(longType){
        DateType.SECOND->{
            second = this
            minute = second / 60
            hours = minute / 60
            days = hours / 24
            months = days / 30
            years = months / 12

            second -= minute * 60
            minute -= hours * 60
            hours -= days * 24
            days -= months * 30
            months -= years * 12
        }
        DateType.MINUTE->{
            minute = this
            hours = minute / 60
            days = hours / 24
            months = days / 30
            years = months / 12

            minute -= hours * 60
            hours -= days * 24
            days -= months * 30
            months -= years * 12
        }
        DateType.HOUR->{
            hours = this
            days = hours / 24
            months = days / 30
            years = months / 12

            hours -= days * 24
            days -= months * 30
            months -= years * 12
        }
        DateType.DAY->{
            days = this
            months = days / 30
            years = months / 12

            days -= months * 30
            months -= years * 12
        }
        DateType.MONTH->{
            months = this
            years = months / 12

            months -= years * 12
        }
        DateType.YEAR->{
            years = this
        }
    }

    replaceDate.forEach {
        when(it){
            DateType.SECOND-> second = 0
            DateType.MINUTE-> minute = 0
            DateType.HOUR-> hours = 0
            DateType.DAY-> days = 0
            DateType.MONTH-> months = 0
            DateType.YEAR-> years = 0
        }
    }

    val dateStr = when(toDateType){
        DateType.SECOND->{
            (((((years * 12 + months) * 30 + days) * 24 + hours) * 60 + minute) * 60 + second).isZero("秒")
        }
        DateType.MINUTE->{
            ((((years * 12 + months) * 30 + days) * 24 + hours) * 60 + minute).isZero("分") + second.isZero("秒")
        }
        DateType.HOUR->{
            (((years * 12 + months) * 30 + days) * 24 + hours).isZero("時${if (includeDuration) "間" else ""}") + minute.isZero("分") + second.isZero("秒")
        }
        DateType.DAY->{
            ((years * 12 + months) * 30 + days).isZero("日") + hours.isZero("時${if (includeDuration) "間" else ""}") + minute.isZero("分") + second.isZero("秒")
        }
        DateType.MONTH->{
            (years * 12 + months).isZero("${if (includeDuration) "ヵ" else ""}月") + days.isZero("日") + hours.isZero("時${if (includeDuration) "間" else ""}") + minute.isZero("分") + second.isZero("秒")
        }
        DateType.YEAR->{
            years.isZero("年") + months.isZero("${if (includeDuration) "ヵ" else ""}月") + days.isZero("日") + hours.isZero("時${if (includeDuration) "間" else ""}") + minute.isZero("分") + second.isZero("秒")
        }
    }

    return dateStr.ifBlank { null }
}

private fun Long.isZero(appendString: String): String {
    return if (this == 0L){
        ""
    } else {
        "${this}${appendString}"
    }
}