package tororo1066.tororopluginapi.lang

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * 言語を簡単にいじれるようにしたクラス
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
        defaultLanguage = plugin.config.getString("defaultLanguage","en_us")!!
        val file = File(plugin.dataFolder.path + "/LangFolder/")
        if (!file.exists()) file.mkdirs()
        val langList = plugin.getResource("LangFolder/lang.txt")
        langList?.bufferedReader()?.readLines()?.forEach {
            if (File(file.path + "$it.yml").exists())return@forEach
            plugin.saveResource("LangFolder/${it}.yml",false)
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



    fun loadDefaultFile(file: String): JsonObject {
        val resource = javaClass.getResourceAsStream("/lang/${file}.json")
            ?: throw NullPointerException("${file}.jsonがデフォルトに存在しません(TororoPluginAPI)")
        return Gson().fromJson(resource.bufferedReader().readText(),JsonObject::class.java)?:throw ClassCastException("JsonObjectとして読み込めませんでした(TororoPluginAPI,${file}.json)")
    }

    companion object{


        private val langFile = HashMap<String,YamlConfiguration>()
        var defaultLanguage = "en_us"
        private var prefix = ""
        /**
         * materialを言語名にする
         * @param material Material
         * @return 失敗すると空の文字列が返ってくる
         */
        fun JsonObject.materialToText(material: Material): String {
            val string = StringBuilder()
            if (material.isBlock){
                string.append("block.")
            }else{
                string.append("item.")
            }

            string.append("minecraft.${material.name.toLowerCase()}")

            val getString = this[string.toString()] ?: return ""

            return getString.asString

        }

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

        fun Player.translate(msg: String, vararg value: String): String {
            val lang = langFile[this.locale]
            if (lang == null){
                val defaultLang = langFile[defaultLanguage]
                    ?: return "§cLanguage Error. This Plugin is Not Registered ${defaultLanguage}(default) File."

                return modifyValue(defaultLang.getString(msg,msg)!!,value)
            }
            return modifyValue(lang.getString(msg,msg)!!,value)
        }

        fun defaultTranslate(msg: String, vararg value: String): String {
            val defaultLang = langFile[defaultLanguage]
                ?: return "§cLanguage Error. This Plugin is Not Registered ${defaultLanguage}(default) File."

            return modifyValue(defaultLang.getString(msg,msg)!!,value)
        }

        private fun modifyValue(msg: String, value: Array<out String>): String {
            var modifyString = msg
            value.forEachIndexed { index, string ->
                modifyString = modifyString.replace("{${index}}",string)
            }
            return modifyString
        }
    }


}