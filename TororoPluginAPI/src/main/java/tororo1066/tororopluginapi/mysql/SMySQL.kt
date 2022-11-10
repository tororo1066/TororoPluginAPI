package tororo1066.tororopluginapi.mysql

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.sql.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * MySQLを楽に使えるクラス
 *
 * [MySQLドキュメント](https://dev.mysql.com/doc/refman/8.0/ja/)
 * @param plugin JavaPlugin.
 */
class SMySQL(val plugin : JavaPlugin) {

    private val host : String = plugin.config.getString("mysql.host")?:throw NullPointerException("hostが指定されていません")
    private val port : String = plugin.config.getString("mysql.port")?:throw NullPointerException("portが指定されていません")
    private val pass : String = plugin.config.getString("mysql.pass")?:throw NullPointerException("passが指定されていません")
    private val db : String = plugin.config.getString("mysql.db")?:throw NullPointerException("dbが指定されていません")
    private val user : String = plugin.config.getString("mysql.user")?:throw NullPointerException("userが指定されていません")

    private var conn : Connection? = null
    private var stmt : Statement? = null

    private val thread: ExecutorService = Executors.newCachedThreadPool()

    /**
     * データベースを開く
     *
     * 基本的に使わない
     */
    fun open(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db + "?useSSL=false", this.user, this.pass)
        }catch (e : Exception){
            Bukkit.getLogger().warning(e.stackTraceToString())
        }
    }

    /**
     * データベースを閉じる
     *
     * [query]のみで使う
     */
    fun close(){
        conn?.close()
    }

    /**
     * queryを実行する
     * ```java
     * //例 Java
     * if (mysql.execute("insert into test_db (name) values('test')")){
     *   //成功時
     * } else {
     *   //失敗時
     * }
     * ```
     * ```kotlin
     * //例 Kotlin
     * if (mysql.execute("insert into test_db (name) values('test')")){
     *   //成功時
     * } else {
     *   //失敗時
     * }
     * ```
     * @param query クエリ文
     * @return [Boolean]
     */
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
            Bukkit.getLogger().warning("ExecuteError：Error Code(${e.errorCode})\nError Message\n${e.message}")
            Bukkit.getLogger().warning(query)
            false
        } finally {
            stmt?.close()
            conn?.close()
        }
    }

    /**
     * queryを実行して結果を取る
     *
     * これだけResultSetを使い終わった後に[close]が必要
     * ```java
     * //例 Java
     * ResultSet rs = mysql.query("select * from test_db");
     * ```
     * ```kotlin
     * //例 Kotlin
     * val rs = mysql.query("select * from test_db")
     * ```
     * @param query クエリ文
     * @return [ResultSet(取得に失敗した場合null)][ResultSet]
     */
    fun query(query : String): ResultSet? {
        open()
        if (conn == null){
            return null
        }

        return try {
            stmt = conn!!.createStatement()
            stmt!!.executeQuery(query)
        } catch (e : SQLException) {
            Bukkit.getLogger().warning("QueryError：Error Code(${e.errorCode})\nError Message\n${e.message}")
            Bukkit.getLogger().warning(query)
            null
        }
    }

    /**
     * queryを実行して結果を取る
     * ```java
     * //例 Java
     * ArrayList<SMySQLResultSet> rs = mysql.sQuery("select * from test_db");
     * ```
     * ```kotlin
     * //例 Kotlin
     * val rs = mysql.sQuery("select * from test_db")
     * ```
     * @param query クエリ文
     * @return [SMySQLResultSetのlist(取得に失敗した場合空)][SMySQLResultSet]
     */
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


    /**
     * 非同期でqueryを実行する
     * ```java
     * //例 Java
     * if (mysql.asyncExecute("insert into test_db (name) values('test')")){
     *   //成功時
     * } else {
     *   //失敗時
     * }
     * ```
     * ```kotlin
     * //例 Kotlin
     * if (mysql.asyncExecute("insert into test_db (name) values('test')")){
     *   //成功時
     * } else {
     *   //失敗時
     * }
     * ```
     * @param query クエリ文
     * @return [Boolean]
     */
    fun asyncExecute(query: String): Boolean {
        return try {
            thread.submit(Callable { execute(query) }).get()
        }catch (e : Exception){
            false
        }
    }

    /**
     * 非同期でqueryを実行して結果を取る
     * ```java
     * //例 Java
     * ArrayList<SMySQLResultSet> rs = mysql.asyncQuery("select * from test_db");
     * ```
     * ```kotlin
     * //例 Kotlin
     * val rs = mysql.asyncQuery("select * from test_db")
     * ```
     * @param query クエリ文
     * @return [SMySQLResultSetのlist(取得に失敗した場合空)][SMySQLResultSet]
     */
    fun asyncQuery(query: String): ArrayList<SMySQLResultSet> {
        return try {
            thread.submit(Callable { sQuery(query) }).get()
        }catch (e : Exception){
            return arrayListOf()
        }
    }





}