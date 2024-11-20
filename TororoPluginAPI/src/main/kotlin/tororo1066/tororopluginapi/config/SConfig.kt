package tororo1066.tororopluginapi.config

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.otherUtils.UsefulUtility
import java.io.File
import java.util.concurrent.CompletableFuture

/**
 * Config関連のクラス
 * @param plugin JavaPlugin.
 */
class SConfig(val plugin: JavaPlugin) {

    private var alwaysPath = ""

    /**
     * @param alwaysPath 常に使うパス(plugins/<プラグイン名>/は指定しなくていい)
     */
    constructor(plugin: JavaPlugin, alwaysPath: String): this(plugin){
        this.alwaysPath = alwaysPath
    }

    /**
     * 常に使うパスを指定する
     * @param alwaysPath 常に使うパス(plugins/<プラグイン名>/は指定しなくていい)
     */
    fun setAlwaysPath(alwaysPath: String): SConfig {
        this.alwaysPath = alwaysPath
        return this
    }

    /**
     * configファイルを取得する
     * ```java
     * //例 Java
     * YamlConfiguration testConfig = sConfig.getConfig("testfolder/test");
     * ```
     * ```kotlin
     * //例 Kotlin
     * val testConfig = sConfig.getConfig("testfolder/test")
     * ```
     * @param path ファイルのパス(.ymlは必要ない)
     * @return [YamlConfiguration(存在しなかったらnull)][YamlConfiguration]
     */
    fun getConfig(path: String): YamlConfiguration? {
        val file = File(plugin.dataFolder.path + "/${alwaysPath}/${path}.yml")
        if (!file.exists())return null
        return YamlConfiguration.loadConfiguration(file)
    }

    fun asyncGetConfig(path: String): CompletableFuture<YamlConfiguration?> {
        return CompletableFuture.supplyAsync {
            getConfig(path)
        }
    }

    fun getOrCreateConfig(path: String): YamlConfiguration {
        val file = File(plugin.dataFolder.path + "/${alwaysPath}/${path}.yml")
        if (!file.exists()){
            file.parentFile.mkdirs()
            val resource = plugin.getResource("${if (alwaysPath.isNotEmpty()) "$alwaysPath/" else ""}$path.yml")
            if (resource != null){
                plugin.saveResource("${if (alwaysPath.isNotEmpty()) "$alwaysPath/" else ""}$path.yml",false)
            } else {
                file.createNewFile()
            }
        }
        return YamlConfiguration.loadConfiguration(file)
    }

    fun asyncGetOrCreateConfig(path: String): CompletableFuture<YamlConfiguration> {
        return CompletableFuture.supplyAsync {
            getOrCreateConfig(path)
        }
    }

    /**
     * configファイルのリストを取得する
     * ```java
     * //例 Java
     * List<YamlConfiguration> testConfigs = sConfig.getConfigList("testfolder");
     * ```
     * ```kotlin
     * //例 Kotlin
     * val testConfigs = sConfig.getConfigList("testfolder")
     * ```
     * @param path フォルダのパス
     * @return [YamlConfigurationのリスト(存在しなかったら空)][YamlConfiguration]
     */
    fun getConfigList(path: String): List<YamlConfiguration>{
        val file = File(plugin.dataFolder.path + "/${alwaysPath}/${path}/")
        val yaml = ArrayList<YamlConfiguration>()
        (file.listFiles()?:return emptyList()).forEach {
            if (it.extension != "yml")return@forEach
            yaml.add(YamlConfiguration.loadConfiguration(it))
        }
        return yaml
    }

    /**
     * configファイルを保存する
     *
     * フォルダやファイルがなくても基本的に生成してくれる
     * ```java
     * //例 Java
     * YamlConfiguration testConfig = sConfig.getConfig("testfolder/test");
     * testConfig.set("test","test");
     * sConfig.saveConfig(testConfig,"testfolder/test");
     * ```
     * ```kotlin
     * //例 Kotlin
     * val testConfig = sConfig.getConfig("testfolder/test")
     * testConfig.set("test","test")
     * sConfig.saveConfig(testConfig,"testfolder/test")
     * ```
     * @param configuration [YamlConfiguration]
     * @param path ファイルのパス(.ymlは必要ない)
     * @return 成功したらtrue、失敗したらfalse
     */
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

    fun asyncSaveConfig(configuration: YamlConfiguration, path: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            saveConfig(configuration, path)
        }
    }

    /**
     * configファイルが存在するか確かめる
     * ```java
     * //例 Java
     * if (sConfig.exists("testfolder/test")){
     *   code...
     * }
     * ```
     * ```kotlin
     * //例 Kotlin
     * if (sConfig.exists("testfolder/test")){
     *   code...
     * }
     * ```
     * @param path ファイルのパス(.ymlは必要ない)
     * @return configファイルが存在するかどうか
     */
    fun exists(path: String): Boolean {
        val file = File(plugin.dataFolder.path + File.separator + alwaysPath + File.separator + "${path}.yml")
        return file.exists()
    }

    fun mkdirs(path: String): Boolean {
        val file = File(plugin.dataFolder.path + File.separator + alwaysPath + File.separator + path)
        return UsefulUtility.sTry({file.mkdirs()},{false})
    }

    /**
     * configファイルをFileに変換する
     * ```java
     * //例 Java
     * YamlConfiguration testConfig = sConfig.getConfig("testfolder/test");
     * File file = testConfig.toFile();
     * ```
     * ```kotlin
     * //例 Kotlin
     * val testConfig = sConfig.getConfig("testfolder/test")
     * val file = testConfig.toFile()
     * ```
     * @return [File]
     */
    fun YamlConfiguration.toFile(): File {
        return File(currentPath)
    }

    fun loadAllFiles(folder: File): List<File> {
        if (!folder.exists()) return emptyList()
        val fileList = ArrayList<File>()
        (folder.listFiles()?:return emptyList()).forEach {
            if (it.isDirectory){
                fileList.addAll(loadAllFiles(it))
            } else {
                fileList.add(it)
            }
        }
        return fileList
    }

    fun loadAllFiles(path: String): List<File> {
        val file = File(plugin.dataFolder.path + File.separator + alwaysPath + File.separator + path + File.separator)
        if (!file.exists()) return emptyList()
        return loadAllFiles(file)
    }
}