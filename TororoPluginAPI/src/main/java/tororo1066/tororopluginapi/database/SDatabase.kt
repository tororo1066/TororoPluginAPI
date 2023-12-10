package tororo1066.tororopluginapi.database

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.mongo.SMongo
import tororo1066.tororopluginapi.database.mysql.SMySQL
import tororo1066.tororopluginapi.database.sqlite.SSQLite
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.function.Supplier

abstract class SDatabase(val plugin: JavaPlugin) {

    private var configFile: String? = null
    private var configPath: String? = null
    protected val host: String?
    protected var port: Int? = null
    protected val pass: String?
    protected val db: String?
    protected val user: String?
    protected val url: String?

    private val thread: ExecutorService = Executors.newCachedThreadPool()

    abstract val isMongo: Boolean

    constructor(plugin: JavaPlugin, configFile: String?, configPath: String?): this(plugin){
        this.configFile = configFile
        this.configPath = configPath
    }

    init {
        var yml = plugin.config
        if (configFile != null){
            yml = YamlConfiguration.loadConfiguration(File(plugin.dataFolder.path + File.separator + configFile))
        }
        if (configPath != null){
            host = yml.getString("$configPath.host")
            if (yml.isSet("$configPath.port")){
                port = yml.getInt("$configPath.port")
            }
            pass = yml.getString("$configPath.pass")
            user = yml.getString("$configPath.user")
            db = yml.getString("$configPath.db")
            url = yml.getString("$configPath.url")
        } else {
            host = yml.getString("database.host")
            if (yml.isSet("database.port")){
                port = yml.getInt("database.port")
            }
            pass = yml.getString("database.pass")
            user = yml.getString("database.user")
            db = yml.getString("database.db")
            url = yml.getString("database.url")
        }

    }


    abstract fun open(): Any

    abstract fun createTable(table: String, map: Map<String, SDBVariable<*>>): Boolean

    abstract fun insert(table: String, map: Map<String, Any>): Boolean

    abstract fun select(table: String, condition: SDBCondition = SDBCondition.empty()): List<SDBResultSet>

    abstract fun update(table: String, update: Any, condition: SDBCondition = SDBCondition.empty()): Boolean

    abstract fun delete(table: String, condition: SDBCondition = SDBCondition.empty()): Boolean

    abstract fun query(query: String): List<SDBResultSet>

    fun asyncCreateTable(table: String, map: Map<String, SDBVariable<*>>): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ createTable(table, map) }, thread)
    }

    fun asyncInsert(table: String, map: Map<String, Any>): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ insert(table, map) }, thread)
    }

    fun asyncSelect(table: String, condition: SDBCondition = SDBCondition.empty()): CompletableFuture<List<SDBResultSet>> {
        return CompletableFuture.supplyAsync({ select(table, condition) }, thread)
    }

    fun asyncUpdate(table: String, update: Any, condition: SDBCondition): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ update(table, update, condition) }, thread)
    }

    fun asyncDelete(table: String, condition: SDBCondition = SDBCondition.empty()): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ delete(table, condition) }, thread)
    }

    fun asyncQuery(query: String): CompletableFuture<List<SDBResultSet>> {
        return CompletableFuture.supplyAsync({ query(query) }, thread)
    }

    fun backGroundCreateTable(table: String, map: Map<String, SDBVariable<*>>, callback: (Boolean) -> Unit = {}){
        thread.execute {
            callback(createTable(table, map))
        }
    }

    fun backGroundInsert(table: String, map: Map<String, Any>, callback: (Boolean) -> Unit = {}){
        thread.execute {
            callback(insert(table, map))
        }
    }

    fun backGroundSelect(table: String, condition: SDBCondition = SDBCondition.empty(), callback: (List<SDBResultSet>) -> Unit = {}){
        thread.execute {
            callback(select(table, condition))
        }
    }

    fun backGroundUpdate(table: String, update: Any, condition: SDBCondition = SDBCondition.empty(), callback: (Boolean) -> Unit = {}){
        thread.execute {
            callback(update(table, update, condition))
        }
    }

    fun backGroundDelete(table: String, condition: SDBCondition = SDBCondition.empty(), callback: (Boolean) -> Unit = {}){
        thread.execute {
            callback(delete(table, condition))
        }
    }

    fun backGroundQuery(query: String, callback: (List<SDBResultSet>) -> Unit = {}){
        thread.execute {
            callback(query(query))
        }
    }


    companion object {
        fun Any.toSQLVariable(type: SDBVariable.VariableType<*>): String {
            return SDBCondition.modifySQLString(type, this)
        }

        fun newInstance(plugin: JavaPlugin): SDatabase {
            return when(plugin.config.getString("database.type")?:"mysql"){
                "sqlite"-> SSQLite(plugin)
                "mongodb"-> SMongo(plugin)
                else-> SMySQL(plugin)
            }
        }

        fun newInstance(plugin: JavaPlugin, configFile: String?, configPath: String?): SDatabase {
            var config = plugin.config
            if (configFile != null){
                config = YamlConfiguration.loadConfiguration(File(plugin.dataFolder.path + File.separator + configFile))
            }

            return when(config.getString("${configPath?:"database"}.type")?:"mysql"){
                "sqlite"-> SSQLite(plugin, configFile, configPath)
                "mongodb"-> SMongo(plugin, configFile, configPath)
                else-> SMySQL(plugin, configFile, configPath)
            }
        }
    }
}