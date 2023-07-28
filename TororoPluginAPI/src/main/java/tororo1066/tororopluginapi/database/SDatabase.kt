package tororo1066.tororopluginapi.database

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.mongo.SMongo
import tororo1066.tororopluginapi.database.mysql.SMySQLAlpha
import tororo1066.tororopluginapi.database.sqlite.SSQLite
import tororo1066.tororopluginapi.mysql.ultimate.USQLVariable
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

    private val thread: ExecutorService = Executors.newCachedThreadPool()

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
        } else {
            host = yml.getString("database.host")
            if (yml.isSet("database.port")){
                port = yml.getInt("database.port")
            }
            pass = yml.getString("database.pass")
            user = yml.getString("database.user")
            db = yml.getString("database.db")
        }

    }


    abstract fun open(): Any

    abstract fun insert(table: String, map: Map<String, Any>): Boolean

    abstract fun select(table: String, condition: SDBCondition = SDBCondition.empty()): List<SDBResultSet>

    abstract fun update(table: String, map: Map<String, Any>, condition: SDBCondition = SDBCondition.empty()): Boolean

    abstract fun delete(table: String, condition: SDBCondition = SDBCondition.empty()): Boolean

    fun asyncInsert(table: String, map: Map<String, Any>): Future<Boolean> {
        return thread.submit(Callable { insert(table, map) })
    }

    fun asyncSelect(table: String, condition: SDBCondition = SDBCondition.empty()): Future<List<SDBResultSet>> {
        return thread.submit(Callable { select(table, condition) })
    }

    fun asyncUpdate(table: String, map: Map<String, Any>, condition: SDBCondition): Future<Boolean> {
        return thread.submit(Callable { update(table, map, condition) })
    }

    fun asyncDelete(table: String, condition: SDBCondition = SDBCondition.empty()): Future<Boolean> {
        return thread.submit(Callable { delete(table, condition) })
    }

    companion object {
        fun Any.toSQLVariable(type: USQLVariable.VariableType<*>): String {
            return SDBCondition.modifySQLString(type, this)
        }

        fun newInstance(plugin: JavaPlugin): SDatabase {
            return when(plugin.config.getString("database.type")?:"mysql"){
                "sqlite"-> SSQLite(plugin)
                "mongodb"-> SMongo(plugin)
                else-> SMySQLAlpha(plugin)
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
                else-> SMySQLAlpha(plugin, configFile, configPath)
            }
        }
    }
}