package tororo1066.tororopluginapi.database

import org.bson.Document
import java.time.LocalDateTime
import java.util.Date

class SDBResultSet(val result : HashMap<String,Any?>){
    fun getString(name: String): String {
        return result[name].toString()
    }

    fun getInt(name: String): Int {
        return result[name] as Int
    }

    fun getFloat(name: String): Float {
        if (result[name] is Double) return (result[name] as Double).toFloat()
        return result[name] as Float
    }

    fun getDouble(name: String): Double {
        if (result[name] is Float) return (result[name] as Float).toDouble()
        return result[name] as Double
    }

    fun getBoolean(name: String): Boolean {
        return result[name] as Boolean
    }

    fun getLocalDateTime(name: String): LocalDateTime {
        return result[name] as LocalDateTime
    }

    fun getDate(name: String): Date {
        return result[name] as Date
    }

    fun getType(name: String): Class<*> {
        return result[name]!!.javaClass
    }

    fun getLong(name: String): Long {
        return result[name] as Long
    }

    fun getObject(name: String): Any {
        return result[name]!!
    }

    fun getBytes(name: String): ByteArray {
        return result[name] as ByteArray
    }

    inline fun <reified T> getList(name: String): List<T> {
        if (T::class.java == SDBResultSet::class.java){
            val list = result[name] as List<Document>
            val resultList = ArrayList<SDBResultSet>()
            for (document in list){
                resultList.add(SDBResultSet(HashMap(document)))
            }
            return resultList as List<T>
        }
        return result[name] as List<T>
    }

    fun getDeepResult(name: String): SDBResultSet {
        return SDBResultSet(HashMap(result[name] as Document))
    }

    fun getNullableString(name: String): String? {
        return result[name]?.toString()
    }

    fun getNullableInt(name: String): Int? {
        return result[name] as? Int
    }

    fun getNullableFloat(name: String): Float? {
        if (result[name] is Double) return (result[name] as Double).toFloat()
        return result[name] as? Float
    }

    fun getNullableDouble(name: String): Double? {
        return result[name] as? Double
    }

    fun getNullableBoolean(name: String): Boolean? {
        return result[name] as? Boolean
    }

    fun getNullableDate(name: String): LocalDateTime? {
        return result[name] as? LocalDateTime
    }

    fun getNullableType(name: String): Class<*>? {
        return result[name]?.javaClass
    }

    fun getNullableLong(name: String): Long? {
        return result[name] as? Long
    }

    fun getNullableObject(name: String): Any? {
        return result[name]
    }

    fun getNullableBytes(name: String): ByteArray? {
        return result[name] as? ByteArray
    }

    fun getNullableDeepResult(name: String): SDBResultSet? {
        return result[name]?.let { SDBResultSet(HashMap(it as Document)) }
    }

    inline fun <reified T> getNullableList(name: String): List<T>? {
        if (T::class.java == SDBResultSet::class.java){
            val list = result[name] as? List<Document> ?: return null
            val resultList = ArrayList<SDBResultSet>()
            for (document in list){
                resultList.add(SDBResultSet(HashMap(document)))
            }
            return resultList as List<T>
        }
        return result[name] as? List<T>
    }

}