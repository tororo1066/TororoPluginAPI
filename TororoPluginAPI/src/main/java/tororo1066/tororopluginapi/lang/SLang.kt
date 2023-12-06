package tororo1066.tororopluginapi.lang

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.Executors

/**
 * 言語によってメッセージが変わるクラス
 *
 * resourcesの下にLangFolderというフォルダを作り、その下に言語ファイルを置く
 *
 * LangFolderの下にlang.txtを作り、使用する言語を書く
 * ```txt
 * //例
 * en_us
 * ja_jp
 * ```
 * ```txt
 * //ファイル構成
 * resources
 *   |-> LangFolder
 *       |-> en_us.yml
 *       |-> ja_jp.yml
 *       |-> lang.txt
 * ```
 */
class SLang(private val plugin: JavaPlugin) {

    constructor(plugin: JavaPlugin, prefixString: String): this(plugin) {
        prefix = prefixString
    }

    init {
        init()
    }
    fun init(){
        langFile.clear()

        if (!File(plugin.dataFolder.path + "/config.yml").exists())return
        plugin.reloadConfig()
        defaultLanguage = plugin.config.getString("defaultLanguage","en_us")!!
        val overwrite = plugin.config.getBoolean("langOverwrite")
        val file = File(plugin.dataFolder.path + "/LangFolder/")
        if (!file.exists()) file.mkdirs()
        val langList = plugin.getResource("LangFolder/lang.txt")
        langList?.bufferedReader()?.readLines()?.forEach {
            if (File(file.path + "$it.yml").exists() && !overwrite)return@forEach
            plugin.saveResource("LangFolder/${it}.yml",true)
        }

        for (config in file.listFiles()?:return){
            if (config.extension != "yml")continue
            val yml = YamlConfiguration.loadConfiguration(config)
            langFile[config.nameWithoutExtension] = yml
        }
    }


    /**
     * 言語ファイルを取得する。
     * @param lang Lang名
     * @return YamlConfiguration
     */
    fun getLangFile(lang : String): YamlConfiguration? {
        return langFile[lang]
    }

    companion object{


        val langFile = HashMap<String,YamlConfiguration>()
        val mcLangFile = HashMap<String,JsonObject>()
        private val es = Executors.newSingleThreadExecutor()
        var defaultLanguage = "en_us"
        private var prefix = ""

        init {
            mcLangInit()
        }

        /**
         * [CommandSender(Player)][CommandSender]に言語によって変わるメッセージを送る
         *
         * プレイヤーの場合、そのプレイヤーの言語が優先される(存在しなかったらデフォルト)
         * ```java
         * //例 Java
         * SLang.sendTranslateMsg(p,"test_message");
         * ```
         *
         * ```kotlin
         * //例 Kotlin
         * p.sendTranslateMsg("test_message")
         * ```
         *
         * @param msg configのパス
         * @param value {<数字>}の文字を置き換える
         */
        @Suppress("DEPRECATION")
        fun CommandSender.sendTranslateMsg(msg: String, vararg value: String){
            if (this !is Player){
                val defaultLang = langFile[defaultLanguage]
                if (defaultLang == null){
                    this.sendMessage("§cLanguage Error. This Plugin is Not Registered ${defaultLanguage}(default) File.")
                    return
                }
                this.sendMessage(prefix + modifyValue(defaultLang.getString(msg,msg)!!,value))
                return
            }
            val lang = langFile[this.locale]
            if (lang == null){
                val defaultLang = langFile[defaultLanguage]
                if (defaultLang == null){
                    this.sendMessage("§cLanguage Error. This Plugin is Not Registered ${defaultLanguage}(default) File.")
                    return
                }
                this.sendMessage(prefix + modifyValue(defaultLang.getString(msg,msg)!!,value))
                return
            }
            this.sendMessage(prefix + modifyValue(lang.getString(msg,msg)!!,value))
        }

        /**
         * 言語によってメッセージを変更する
         *
         * ```java
         * //例 Java
         * String translate = SLang.translate("test_message");
         * ```
         *
         * ```kotlin
         * //例 Kotlin
         * val translate = translate("test_message")
         * ```
         *
         * @param msg configのパス
         * @param value {<数字>}の文字を置き換える
         */
        fun translate(msg: String, vararg value: Any): String {
            val defaultLang = langFile[defaultLanguage]
                ?: return "§cLanguage Error. This Plugin is Not Registered ${defaultLanguage}(default) File."

            return modifyValue(defaultLang.getString(msg,msg)!!,value)
        }

        @Suppress("DEPRECATION")
        fun translate(msg: String, p: Player, vararg value: Any): String {
            val lang = langFile[p.locale]
                ?: return translate(msg,*value)

            return modifyValue(lang.getString(msg,msg)!!,value)
        }

        private fun modifyValue(msg: String, value: Array<out Any>): String {
            var modifyString = msg
            value.forEachIndexed { index, any ->
                modifyString = modifyString.replace("{${index}}",any.toString())
            }
            return modifyString.replace("&","§")
        }

        private fun mcLangInit(){
            mcLangFile.clear()

            val mcLangFolder = File("plugins/TororoPluginAPI/lang/${Bukkit.getMinecraftVersion()}/")
            if (!mcLangFolder.exists())mcLangFolder.mkdirs()
            mcLangFolder.listFiles()?.let { files ->
                files.filter { it?.extension == "json" }.forEach {
                    val lang = it.nameWithoutExtension
                    val json = Gson().fromJson(it.readText(),JsonObject::class.java)
                    mcLangFile[lang] = json
                }
            }
        }

        private var downloading = HashMap<String,Boolean>()

        fun getMcLangFile(lang: String, sender: CommandSender? = Bukkit.getConsoleSender()): JsonObject? {
            if (mcLangFile.containsKey(lang)){
                return mcLangFile[lang]
            }

            sender?.sendMessage("§aMinecraft Language File($lang) Not Found.")

            downloadMcLangFile(lang,sender) {
                sender?.sendMessage("§aPlease Try Again.")
            }

            return null
        }

        fun downloadMcLangFile(lang: String, sender: CommandSender? = Bukkit.getConsoleSender(), callback: (JsonObject) -> Unit){
            if (mcLangFile.containsKey(lang)){
                callback(mcLangFile[lang]!!)
                return
            }
            if (downloading[lang] == true){
                sender?.sendMessage("§cAnother download is in progress. Try again later.")
                return
            }
            downloading[lang] = true
            sender?.sendMessage("§aDownloading Minecraft Language File($lang)...")
            es.execute {
                try {
                    val version = Bukkit.getMinecraftVersion()
                    val request = URL("https://launchermeta.mojang.com/mc/game/version_manifest.json").readText()
                    val json = Gson().fromJson(request,JsonObject::class.java)
                    val versionList = json.getAsJsonArray("versions")
                    val url = versionList.first { it.asJsonObject.get("id").asString == version }.asJsonObject.get("url").asString

                    val versionJson = Gson().fromJson(URL(url).readText(),JsonObject::class.java)
                    val assetsUrl = versionJson.getAsJsonObject("assetIndex").get("url").asString

                    val assetsJson = Gson().fromJson(URL(assetsUrl).readText(),JsonObject::class.java)
                    val langHash = assetsJson.getAsJsonObject("objects").get("minecraft/lang/$lang.json").asJsonObject.get("hash").asString

                    val langContent = URL("https://resources.download.minecraft.net/${langHash.substring(0,2)}/${langHash}").readText()
                    val langJson = Gson().fromJson(langContent,JsonObject::class.java)
                    mcLangFile[lang] = langJson
                    val file = File("plugins/TororoPluginAPI/lang/${version}/$lang.json")
                    if (!file.exists()){
                        if (!file.parentFile.exists()) file.parentFile.mkdirs()
                        file.createNewFile()
                    }
                    file.writeText(Gson().toJson(langJson))

                    sender?.sendMessage("§aDownloaded Minecraft Language File($lang).")
                    callback(langJson)
                } catch (e: Exception) {
                    sender?.sendMessage("§cFailed to download Minecraft Language File($lang).")
                    sender?.sendMessage("§cSee the console for details.")
                    e.printStackTrace()
                } finally {
                    downloading[lang] = false
                }
            }
        }
    }


}