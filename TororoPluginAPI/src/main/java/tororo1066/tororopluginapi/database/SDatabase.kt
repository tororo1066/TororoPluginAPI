package tororo1066.tororopluginapi.database

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.mongo.SMongo
import tororo1066.tororopluginapi.database.mysql.SMySQL
import tororo1066.tororopluginapi.database.sqlite.SSQLite
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

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

    fun asyncCreateTable(table: String, map: Map<String, SDBVariable<*>>): Future<Boolean> {
        return thread.submit(Callable { createTable(table, map) })
    }

    fun asyncInsert(table: String, map: Map<String, Any>): Future<Boolean> {
        return thread.submit(Callable { insert(table, map) })
    }

    fun asyncSelect(table: String, condition: SDBCondition = SDBCondition.empty()): Future<List<SDBResultSet>> {
        return thread.submit(Callable { select(table, condition) })
    }

    fun asyncUpdate(table: String, update: Any, condition: SDBCondition): Future<Boolean> {
        return thread.submit(Callable { update(table, update, condition) })
    }

    fun asyncDelete(table: String, condition: SDBCondition = SDBCondition.empty()): Future<Boolean> {
        return thread.submit(Callable { delete(table, condition) })
    }

    fun asyncQuery(query: String): Future<List<SDBResultSet>> {
        return thread.submit(Callable { query(query) })
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