package tororo1066.tororopluginapi.database

import java.text.MessageFormat
import java.util.logging.Formatter
import java.util.logging.LogRecord

class SDatabaseLoggerFormatter: Formatter() {
    override fun format(record: LogRecord): String {
        // 2021-08-07 12:00:00 [INFO] [Database] Message
        return MessageFormat.format("{0,date,yyyy-MM-dd HH:mm:ss} [{1}] [{2}] {3}\n",
            record.millis,
            record.level.localizedName,
            record.loggerName,
            record.message
        )
    }
}