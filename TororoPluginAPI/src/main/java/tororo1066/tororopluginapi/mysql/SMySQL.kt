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

    var useSQLite = false

    private val thread: ExecutorService = Executors.newCachedThreadPool()

    /**
     * データベースを開く
     *
     * 基本的に使わない
     */
    fun open(): Connection {
        val conn: Connection
        try {
            if (useSQLite){
                if (db == null){
                    throw NullPointerException("[SQLite] Database name is empty.")
                }
                Class.forName("org.sqlite.JDBC")
                conn = DriverManager.getConnection("jdbc:sqlite:${plugin.dataFolder.absolutePath.replace("\\","/")}/${db}.db")
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
            throw e
        }

        return conn
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
        val conn = open()
        var stmt: Statement? = null

        return try {
            stmt = conn.createStatement()
            if (!useSQLite) stmt!!.setEscapeProcessing(true)
            stmt!!.execute(query)
            true
        } catch (e : SQLException){
            Bukkit.getLogger().warning("ExecuteError：Error Code(${e.errorCode})\nError Message\n${e.message}")
            Bukkit.getLogger().warning(query)
            false
        } finally {
            stmt?.close()
            conn.close()
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
    fun query(query : String): Triple<Connection,Statement?,ResultSet?> {
        val conn = open()
        var stmt: Statement? = null

        return try {
            stmt = conn.createStatement()
            if (!useSQLite) stmt.setEscapeProcessing(true)
            Triple(conn,stmt,stmt.executeQuery(query))
        } catch (e : SQLException) {
            Bukkit.getLogger().warning("QueryError：Error Code(${e.errorCode})\nError Message\n${e.message}")
            Bukkit.getLogger().warning(query)
            Triple(conn,stmt,null)
        }
    }

    fun asyncCount(query: String): Int {
        return thread.submit(Callable {
            val rs = query(query)
            val resultSet = rs.third?:return@Callable 0
            try {
                resultSet.next()
                resultSet.getInt(1)
            } catch (e: SQLException){
                0
            } finally {
                resultSet.close()
                rs.second?.close()
                rs.first.close()
            }
        }).get()
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
        val rs = query(query)
        val resultSet = rs.third?:return arrayListOf()
        val result = ArrayList<SMySQLResultSet>()
        try {
            while (resultSet.next()){
                val meta = resultSet.metaData
                val data = HashMap<String,Any?>()
                for (i in 1 until meta.columnCount + 1) {
                    val name = meta.getColumnName(i)
                    val obj = resultSet.getObject(name)
                    if (obj == "true" || obj == "false"){
                        data[name] = obj.toString().toBoolean()
                    } else {
                        data[name] = obj
                    }
                }
                result.add(SMySQLResultSet(data))
            }
            return result
        } catch (e : Exception){
            e.printStackTrace()
            return arrayListOf()
        } finally {
            resultSet.close()
            rs.second?.close()
            rs.first.close()
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