package tororo1066.tororopluginapi.mysql

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.NullPointerException
import java.sql.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.security.auth.callback.Callback

/**
 * MySQLを楽に使えるクラス
 *
 * [MySQLドキュメント](https://dev.mysql.com/doc/refman/8.0/ja/)
 * @param plugin JavaPlugin.
 */
class SMySQL(val plugin : JavaPlugin) {

    constructor(plugin: JavaPlugin, useSQLite: Boolean): this(plugin){
        this.useSQLite = useSQLite
    }

    constructor(plugin: JavaPlugin, configFile: String?, configPath: String?, useSQLite: Boolean): this(plugin){
        this.configFile = configFile
        this.configPath = configPath
        this.useSQLite = useSQLite
    }

    private var configFile: String? = null
    private var configPath: String? = null
    private val host: String?
    private val port: String?
    private val pass: String?
    private val db: String?
    private val user: String?
    init {
        var yml = plugin.config
        if (configFile != null){
            yml = YamlConfiguration.loadConfiguration(File(plugin.dataFolder.path + File.separator + configFile))
        }
        if (configPath != null){
            host = yml.getString("$configPath.host")
            port = yml.getString("$configPath.port")
            pass = yml.getString("$configPath.pass")
            user = yml.getString("$configPath.user")
            db = yml.getString("$configPath.db")
        } else {
            host = yml.getString("mysql.host")
            port = yml.getString("mysql.port")
            pass = yml.getString("mysql.pass")
            user = yml.getString("mysql.user")
            db = yml.getString("mysql.db")
        }

    }

    private var useSQLite = false

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
            if (useSQLite){
                if (db == null){
                    throw NullPointerException("[SQLite] Database name is empty.")
                }
                Class.forName("org.sqlite.JDBC")
                conn = DriverManager.getConnection("jdbc:sqlite:${plugin.dataFolder.absolutePath}/${db}.db")
                conn!!.metaData
            } else {
                if (host == null){
                    throw NullPointerException("[MySQL] Host name is empty.")
                }
                if (port == null){
                    throw NullPointerException("[MySQL] Port number is empty.")
                }
                if (pass == null){
                    throw NullPointerException("[MySQL] Password is empty.")
                }
                if (user == null){
                    throw NullPointerException("[MySQL] User name is empty.")
                }
                if (db == null){
                    throw NullPointerException("[MySQL] Database name is empty.")
                }
                Class.forName("com.mysql.cj.jdbc.Driver")
                conn = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.db + "?useSSL=false", this.user, this.pass)
            }
        }catch (e : SQLException){
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

    fun callbackExecute(query: String, callback: (Boolean)-> Unit){
        try {
            thread.execute {
                callback.invoke(execute(query))
            }
        }catch (e: Exception){
            callback.invoke(false)
        }
    }
    fun callbackQuery(query: String, callback: (ArrayList<SMySQLResultSet>)-> Unit){
        try {
            thread.execute {
                callback.invoke(sQuery(query))
            }
        }catch (e: Exception){
            callback.invoke(arrayListOf())
        }
    }





}