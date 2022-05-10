package tororo1066.tororopluginapi.lang

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SConfig
import java.io.File
import java.io.FileWriter

/**
 * 言語を簡単にいじれるようにしたクラス
 */
class SLang(val plugin: JavaPlugin) {

    private val sConfig = SConfig(plugin,"LangFolder")
    private val langFile = HashMap<String,YamlConfiguration>()

    init {
        init()

    }
    fun init(){
        langFile.clear()
        val file = File(plugin.dataFolder.path + "/LangFolder/")
        if (!file.exists()) file.mkdirs()
        val nameFiles = sConfig.plugin.javaClass.getResourceAsStream("/LangFolder/")?.bufferedReader()?.readLines()?:return
        nameFiles.forEach {
            if (sConfig.getConfig(it) != null){
                return@forEach
            }

            val configFile = File(plugin.dataFolder.path + "/LangFolder/${it}.yml")
            configFile.createNewFile()
            val writer = FileWriter(configFile)
            writer.write(sConfig.plugin.javaClass.getResourceAsStream("/LangFolder/${it}")!!.bufferedReader().readText())
            writer.close()
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

    fun Player.sendTranslateMsg(msg: String){
        val lang = langFile[this.locale]
        if (lang == null){
            val defaultLang = langFile["en_us"]
            if (defaultLang == null){
                this.sendMessage("§cLanguage Error. This Plugin Not Registered en_us(default) File.")
                return
            }
            this.sendMessage(defaultLang.getString(msg,msg))
            return
        }
        this.sendMessage(lang.getString(msg,msg))
    }

    fun loadDefaultFile(file: String): JsonObject? {
        val resource = javaClass.getResourceAsStream("/lang/${file}.json")
            ?: throw NullPointerException("${file}.jsonがデフォルトに存在しません")
        return Gson().fromJson(resource.bufferedReader().readText(),JsonObject::class.java)
    }

    companion object{
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
    }


}