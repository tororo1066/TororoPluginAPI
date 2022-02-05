package tororo1066.tororopluginapi.lang

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.Material
import java.io.File
import java.nio.file.Files

class SLang {

    init {
        val file = File("plugins/LangFolder")
        if (!file.exists()){
            file.createNewFile()
        }
    }

    fun getLangFile(file : String): JsonObject {
        return try {
            Gson().fromJson(File("plugins/LangFolder/${file}.json").bufferedReader().readText(),JsonObject::class.java)
        } catch (e : Exception){
            Gson().fromJson(loadDefaultFile(file),JsonObject::class.java)
        }
    }

    fun getJapanese(): JsonObject {
        return getLangFile("ja_jp")
    }

    fun getEnglish(): JsonObject {
        return getLangFile("en_us")
    }

    fun loadDefaultFile(file: String): String {
        val resource = javaClass.getResourceAsStream("/lang/${file}.json")
            ?: throw NullPointerException("${file}.jsonがデフォルトに存在しません")
        return resource.bufferedReader().readText()
    }

    companion object{
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