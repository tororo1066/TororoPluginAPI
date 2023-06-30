package tororo1066.tororopluginapi.lang

import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

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
        var defaultLanguage = "en_us"
        private var prefix = ""

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
         * @param values {<数字>}の文字を置き換える
         */
        fun translate(msg: String, values: Array<out String>): String {
            val defaultLang = langFile[defaultLanguage]
                ?: return "§cLanguage Error. This Plugin is Not Registered ${defaultLanguage}(default) File."

            return modifyValue(defaultLang.getString(msg,msg)!!,values)
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
        fun translate(msg: String, vararg value: String): String {
            return translate(msg, value)
        }

        fun translate(msg: String, p: Player, values: Array<out String>): String {
            return translate(msg, p, values)
        }

        fun translate(msg: String, p: Player, vararg value: String): String {
            val lang = langFile[p.locale]
                ?: return translate(msg,*value)

            return modifyValue(lang.getString(msg,msg)!!,value)
        }

        private fun modifyValue(msg: String, value: Array<out String>): String {
            var modifyString = msg
            value.forEachIndexed { index, string ->
                modifyString = modifyString.replace("{${index}}",string)
            }
            return modifyString.replace("&","§")
        }
    }


}