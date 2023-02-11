package tororo1066.tororopluginapi.mysql

import java.time.LocalDateTime

/**
 * sQuery、asyncQueryで取得できるResultSet
 */
class SMySQLResultSet(val result : HashMap<String,Any?>){
    fun getString(name: String): String {
        return result[name].toString()
    }

    fun getInt(name: String): Int {
        return result[name] as Int
    }

    fun getDouble(name: String): Double {
        return result[name] as Double
    }

    fun getBoolean(name: String): Boolean {
        return result[name] as Boolean
    }

    fun getDate(name: String): LocalDateTime {
        return result[name] as LocalDateTime
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

    fun getNullableString(name: String): String? {
        return result[name]?.toString()
    }

    fun getNullableInt(name: String): Int? {
        return result[name] as? Int
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

}