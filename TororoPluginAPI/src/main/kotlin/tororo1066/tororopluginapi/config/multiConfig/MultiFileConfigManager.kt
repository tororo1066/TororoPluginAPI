package tororo1066.tororopluginapi.config.multiConfig

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.getClasses
import java.io.File
import java.util.concurrent.CompletableFuture

class MultiFileConfigManager(
    val classPath: String,
    val plugin: JavaPlugin = SJavaPlugin.plugin
) {

    val subConfigs = ArrayList<AbstractConfig>()

    fun load(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            val directory = File(plugin.dataFolder, "config")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            subConfigs.clear()
            val classes = plugin.javaClass.protectionDomain.codeSource.location.getClasses(classPath)
            for (clazz in classes) {
                if (AbstractConfig::class.java.isAssignableFrom(clazz) && !clazz.isInterface) {
                    val instance = clazz.getDeclaredConstructor().newInstance() as AbstractConfig
                    val file = File(directory, "${instance.name}.yml")
                    if (!file.exists()) {
                        file.createNewFile()
                        val yml = YamlConfiguration.loadConfiguration(file)
                        instance.saveDefaultConfig(yml)
                        yml.save(file)
                    } else {
                        val yml = YamlConfiguration.loadConfiguration(file)
                        instance.loadConfig(yml)
                    }

                    subConfigs.add(instance)
                }
            }
        }
    }

    inline fun <reified T: AbstractConfig> getConfig(): T? {
        return subConfigs.firstOrNull { it is T } as? T
    }

    inline fun <reified T: AbstractConfig> getOrThrowConfig(): T {
        return getConfig<T>() ?: throw IllegalStateException("Config of type ${T::class.java.name} not found")
    }
}