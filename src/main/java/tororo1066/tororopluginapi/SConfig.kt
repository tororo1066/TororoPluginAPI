package tororo1066.tororopluginapi

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class SConfig(val plugin: JavaPlugin) {

    private var alwaysPath = ""

    constructor(plugin: JavaPlugin, alwaysPath: String): this(plugin){
        this.alwaysPath = alwaysPath
    }

    fun setAlwaysPath(alwaysPath: String){
        this.alwaysPath = alwaysPath
    }

    fun getConfig(path: String): YamlConfiguration? {
        val file = File(plugin.dataFolder.path + "/${alwaysPath}/${path}.yml")
        if (!file.exists())return null
        return YamlConfiguration.loadConfiguration(file)
    }

    fun saveConfig(configuration: YamlConfiguration, path: String): Boolean {
        val file = File(plugin.dataFolder.path + "/${alwaysPath}/${path}.yml")
        if (file.exists()){
            configuration.save(file)
            return true
        }

        val parent = file.parentFile
        if (!parent.exists()){
            if (!parent.mkdirs()) return false
        }

        if (!file.createNewFile()) return false

        configuration.save(file)

        return true
    }

    fun exists(path: String): Boolean {
        val file = File(plugin.dataFolder.path + "/${alwaysPath}/${path}.yml")
        return file.exists()
    }
}