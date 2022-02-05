package tororo1066.tororopluginapi.lang

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.bukkit.Material
import java.io.File

/**
 * 言語を簡単にいじれるようにしたクラス
 */
class SLang {

    init {
        val file = File("plugins/LangFolder")
        if (!file.exists()){
            file.createNewFile()
        }
    }


    /**
     * 言語ファイルを取得する。
     * @param file File名(拡張子不要)
     * @return JsonObject。失敗するとデフォルトを持ってくる。それもなかったらエラーを吐く
     */
    fun getLangFile(file : String): JsonObject {
        return try {
            Gson().fromJson(File("plugins/LangFolder/${file}.json").bufferedReader().readText(),JsonObject::class.java)
        } catch (e : Exception){
            Gson().fromJson(loadDefaultFile(file),JsonObject::class.java)
        }
    }

    /**
     * 日本語の言語ファイルを取得する
     */
    fun getJapanese(): JsonObject {
        return getLangFile("ja_jp")
    }

    /**
     * 英語の言語ファイルを取得する
     */
    fun getEnglish(): JsonObject {
        return getLangFile("en_us")
    }

    fun loadDefaultFile(file: String): String {
        val resource = javaClass.getResourceAsStream("/lang/${file}.json")
            ?: throw NullPointerException("${file}.jsonがデフォルトに存在しません")
        return resource.bufferedReader().readText()
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