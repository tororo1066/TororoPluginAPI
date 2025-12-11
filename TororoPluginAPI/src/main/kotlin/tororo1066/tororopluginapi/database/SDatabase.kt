package tororo1066.tororopluginapi.database

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.mongo.SMongo
import tororo1066.tororopluginapi.database.mysql.SMySQL
import tororo1066.tororopluginapi.database.sqlite.SSQLite
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class SDatabase {

    protected var host: String?
    protected var port: Int? = null
    protected var pass: String?
    protected var db: String?
    protected var user: String?
    protected var url: String?

    protected var plugin: JavaPlugin

    private val thread: ExecutorService = Executors.newCachedThreadPool()

    abstract val isSQL: Boolean
    abstract val isMongo: Boolean

    constructor(plugin: JavaPlugin){
        this.plugin = plugin
        val yml = plugin.config
        host = yml.getString("database.host")
        if (yml.isSet("database.port")){
            port = yml.getInt("database.port")
        }
        pass = yml.getString("database.pass")
        user = yml.getString("database.user")
        db = yml.getString("database.db")
        url = yml.getString("database.url")
    }

    constructor(plugin: JavaPlugin, configFile: String?, configPath: String?): this(plugin){
        this.plugin = plugin
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

    abstract fun createTable(
        table: String,
        map: Map<String, SDBVariable<*>>,
        session: SSession? = null
    ): Boolean

    abstract fun insert(
        table: String,
        map: Map<String, Any?>,
        session: SSession? = null
    ): Boolean

    abstract fun select(
        table: String,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null
    ): List<SDBResultSet>

    abstract fun update(
        table: String,
        update: Any,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null
    ): Boolean

    abstract fun delete(
        table: String,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null
    ): Boolean

    abstract fun query(
        query: String,
        session: SSession? = null
    ): List<SDBResultSet>

    abstract fun execute(
        query: String,
        session: SSession? = null
    ): Boolean

    open fun close() {}

    fun asyncCreateTable(
        table: String,
        map: Map<String, SDBVariable<*>>,
        session: SSession? = null
    ): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ createTable(table, map, session) }, thread)
    }

    fun asyncInsert(
        table: String,
        map: Map<String, Any?>,
        session: SSession? = null
    ): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ insert(table, map, session) }, thread)
    }

    fun asyncSelect(
        table: String,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null
    ): CompletableFuture<List<SDBResultSet>> {
        return CompletableFuture.supplyAsync({ select(table, condition, session) }, thread)
    }

    fun asyncUpdate(
        table: String,
        update: Any,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null
    ): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ update(table, update, condition, session) }, thread)
    }

    fun asyncDelete(
        table: String,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null
    ): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ delete(table, condition, session) }, thread)
    }

    fun asyncQuery(query: String, session: SSession? = null): CompletableFuture<List<SDBResultSet>> {
        return CompletableFuture.supplyAsync({ query(query, session) }, thread)
    }

    fun asyncExecute(query: String, session: SSession? = null): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({ execute(query, session) }, thread)
    }

    fun backGroundCreateTable(
        table: String,
        map: Map<String, SDBVariable<*>>,
        session: SSession? = null,
        callback: (Boolean) -> Unit = {}
    ) {
        thread.execute {
            callback(createTable(table, map, session))
        }
    }

    fun backGroundInsert(
        table: String,
        map: Map<String, Any?>,
        session: SSession? = null,
        callback: (Boolean) -> Unit = {}
    ) {
        thread.execute {
            callback(insert(table, map, session))
        }
    }

    fun backGroundSelect(
        table: String,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null,
        callback: (List<SDBResultSet>) -> Unit = {}
    ) {
        thread.execute {
            callback(select(table, condition, session))
        }
    }

    fun backGroundUpdate(
        table: String,
        update: Any,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null,
        callback: (Boolean) -> Unit = {}
    ) {
        thread.execute {
            callback(update(table, update, condition, session))
        }
    }

    fun backGroundDelete(
        table: String,
        condition: SDBCondition = SDBCondition.empty(),
        session: SSession? = null,
        callback: (Boolean) -> Unit = {}
    ) {
        thread.execute {
            callback(delete(table, condition, session))
        }
    }

    fun backGroundQuery(
        query: String,
        session: SSession? = null,
        callback: (List<SDBResultSet>) -> Unit = {}
    ) {
        thread.execute {
            callback(query(query, session))
        }
    }

    fun backGroundExecute(
        query: String,
        session: SSession? = null,
        callback: (Boolean) -> Unit = {}
    ) {
        thread.execute {
            callback(execute(query, session))
        }
    }

    @OptIn(ExperimentalContracts::class)
    fun transaction(block: SDatabase.(SSession) -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val session = SSession(this)
        try {
            block(session)
        } finally {
            session.close()
        }
    }


    companion object {
        fun Any.toSQLVariable(type: SDBVariable.VariableType<*>): Any {
            return SDBCondition.modifySQLVariable(type, this)
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