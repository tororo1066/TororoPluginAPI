package tororo1066.tororopluginapi.config.multiConfig

import org.bukkit.configuration.ConfigurationSection

abstract class AbstractConfig {

    abstract val name: String

    abstract fun loadConfig(config: ConfigurationSection)

    abstract fun saveDefaultConfig(config: ConfigurationSection)
}