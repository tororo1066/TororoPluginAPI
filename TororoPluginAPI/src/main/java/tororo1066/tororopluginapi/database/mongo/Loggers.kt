//mongodbのLoggersを無効化する(クラスの上書き)
package com.mongodb.internal.diagnostics.logging

import org.bukkit.plugin.java.JavaPlugin


@Suppress("unused")
class Loggers {


    companion object {

        private val PREFIX = "org.mongodb.driver"
        private val plugin = JavaPlugin.getProvidingPlugin(
            Loggers::class.java
        )

        @JvmStatic
        fun getLogger(suffix: String): Logger {
            return LoggerWrapper(plugin.logger)
        }
    }


    private class LoggerWrapper(private val logger: java.util.logging.Logger) : Logger {
        override fun getName(): String {
            return logger.name
        }

        override fun isWarnEnabled(): Boolean {
            return true
        }

        override fun warn(msg: String) {
            logger.warning(msg)
        }

        override fun warn(msg: String, t: Throwable) {
            logger.warning(msg)
        }

        override fun isErrorEnabled(): Boolean {
            return true
        }

        override fun error(msg: String) {
            logger.severe(msg)
        }

        override fun error(msg: String, t: Throwable) {
            logger.severe(msg)
        }

    }

}