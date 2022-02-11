package tororo1066.tororopluginapi

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.sql.*
import java.time.LocalDateTime
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SMySQLResultSet(val result : HashMap<String,Any>){
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

}


class SMySQL(val plugin : JavaPlugin) {

    private val host : String = plugin.config.getString("mysql.host")?:throw NullPointerException("hostが指定されていません")
    private val port : String = plugin.config.getString("mysql.port")?:throw NullPointerException("portが指定されていません")
    private val pass : String = plugin.config.getString("mysql.pass")?:throw NullPointerException("passが指定されていません")
    private val db : String = plugin.config.getString("mysql.db")?:throw NullPointerException("dbが指定されていません")
    private val user : String = plugin.config.getString("mysql.user")?:throw NullPointerException("userが指定されていません")

    private var conn : Connection? = null
    private var stmt : Statement? = null

    private val thread: ExecutorService = Executors.newCachedThreadPool()

    fun open(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db + "?useSSL=false", this.user, this.pass)
        }catch (e : Exception){
            Bukkit.getLogger().warning(e.stackTraceToString())
        }
    }

    fun close(){
        conn?.close()
    }

    fun execute(query : String): Boolean {
        open()
        if (conn == null){
            return false
        }

        return try {
            stmt = conn!!.createStatement()
            stmt!!.execute(query)
            true
        } catch (e : SQLException){
            Bukkit.getLogger().warning("ExecuteError：エラーコード(${e.errorCode})\nエラーメッセージ\n${e.message}")
            Bukkit.getLogger().warning(query)
            false
        } finally {
            stmt?.close()
            conn?.close()
        }
    }

    fun query(query : String): ResultSet? {
        open()
        if (conn == null){
            return null
        }

        return try {
            stmt = conn!!.createStatement()
            stmt!!.executeQuery(query)
        } catch (e : SQLException) {
            Bukkit.getLogger().warning("QueryError：エラーコード(${e.errorCode})\nエラーメッセージ\n${e.message}")
            Bukkit.getLogger().warning(query)
            null
        }
    }

    fun sQuery(query: String): ArrayList<SMySQLResultSet>{
        val rs = query(query)?:return arrayListOf()
        val result = ArrayList<SMySQLResultSet>()
        try {
            while (rs.next()){
                val meta = rs.metaData
                val data = HashMap<String,Any>()
                for (i in 1 until meta.columnCount + 1) {
                    val name = meta.getColumnName(i)
                    data[name] = rs.getObject(name)
                }
                result.add(SMySQLResultSet(data))
            }
            rs.close()
            stmt?.close()
            conn?.close()
            return result
        }catch (e : Exception){
            e.printStackTrace()
            return arrayListOf()
        }
    }

    fun asyncExecute(query: String): Boolean {
        return try {
            thread.submit(Callable { execute(query) }).get()
        }catch (e : Exception){
            false
        }
    }

    fun asyncQuery(query: String): ArrayList<SMySQLResultSet> {
        return try {
            thread.submit(Callable { sQuery(query) }).get()
        }catch (e : Exception){
            return arrayListOf()
        }
    }





}