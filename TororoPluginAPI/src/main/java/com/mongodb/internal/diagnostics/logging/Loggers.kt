//mongodbのLoggersを無効化する(クラスの上書き)
package com.mongodb.internal.diagnostics.logging


@Suppress("unused")
class Loggers {


    companion object {

        @JvmStatic
        fun getLogger(suffix: String): Logger {
            return NoOpLogger(suffix)
        }
    }

}