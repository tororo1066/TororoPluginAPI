package tororo1066.tororopluginapi.database.sqlite

import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.SDBCondition
import tororo1066.tororopluginapi.database.SDBResultSet
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.database.SDatabase
import tororo1066.tororopluginapi.mysql.ultimate.USQLCondition
import java.io.File
import java.sql.*

class SSQLite: SDatabase {

    override val isMongo: Boolean = false

    constructor(plugin: JavaPlugin): super(plugin)
    constructor(plugin: JavaPlugin, configFile: String?, configPath: String?): super(plugin, configFile, configPath)

    override fun open(): Connection {
        val conn: Connection
        try {
            val db = db
            if (db == null){
                throw NullPointerException("[SQLite] Database name is empty.")
            }
            Class.forName("org.sqlite.JDBC")
            val file = File(plugin.dataFolder, "$db.db")
            if (!file.parentFile.exists()){
                file.parentFile.mkdirs()
            }
            conn = DriverManager.getConnection("jdbc:sqlite:${plugin.dataFolder.absolutePath.replace("\\","/")}/${db}.db")
        }catch (e : SQLException){
            throw e
        }

        return conn
    }

    override fun createTable(table: String, map: Map<String, SDBVariable<*>>): Boolean {
        val conn = open()
        val queryBuilder = StringBuilder()
        queryBuilder.append("create table if not exists $table (")
        queryBuilder.append(map.entries.joinToString(",") { it.key + " " + (if (it.value.type is SDBVariable.INT) "integer" else it.value.type.variableName.lowercase()) +
                (if (it.value.index == SDBVariable.Index.PRIMARY) " ${it.value.index!!.tableString}" else "") +
                (if (!it.value.nullable && !it.value.autoIncrement) " not null" else "") +
                (if (it.value.autoIncrement || !it.value.nullable) "" else if (it.value.default == null) " default null" else " default " + USQLCondition.modifySQLString(it.value.type,it.value.default!!)) +
                if (it.value.autoIncrement) " autoincrement" else "" })
        if (map.any { it.value.index == SDBVariable.Index.UNIQUE }){
            queryBuilder.append(",")
            queryBuilder.append(map.filter { it.value.index == SDBVariable.Index.UNIQUE }.keys.joinToString(",") { "unique($it)" })
        }
        queryBuilder.append(")")

        val stmt = conn.createStatement()

        val createIndexQuery = map.filter { it.value.index == SDBVariable.Index.KEY }.map { "create index if not exists ${table}_${it.key}_index on $table(${it.key})" }

        return try {
            if (!stmt.execute(queryBuilder.toString())) {
                return false
            }
            createIndexQuery.forEach {
                if (!stmt.execute(it)) {
                    return false
                }
            }
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            conn.close()
        }
    }

    override fun insert(table: String, map: Map<String, Any?>): Boolean {
        val conn = open()
        val builder = StringBuilder("?")
        for (i in 1 until map.size){
            builder.append(",?")
        }
        val query = "insert into $table (${map.keys.joinToString(",")}) values($builder)"
        val stmt = conn.prepareStatement(query)
        map.values.forEachIndexed { index, any ->
            stmt.setObject(index+1, any)
        }

        return try {
            stmt.execute()
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            conn.close()
        }
    }

    override fun select(table: String, condition: SDBCondition): List<SDBResultSet> {
        return query("select * from $table ${condition.build()}")
    }

    @Suppress("UNCHECKED_CAST")
    override fun update(table: String, update: Any, condition: SDBCondition): Boolean {
        val conn = open()
        val map = update as Map<String, Any>
        val query = "update $table set ${
            map.entries.joinToString(",") { "${it.key} = ?" }
        } ${condition.build()}"
        val stmt = conn.prepareStatement(query)
        map.values.forEachIndexed { index, any ->
            stmt.setObject(index+1, any)
        }

        return try {
            stmt.execute()
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            conn.close()
        }

    }

    override fun delete(table: String, condition: SDBCondition): Boolean {
        val conn = open()
        val query = "delete from $table ?"
        val stmt = conn.prepareStatement(query)
        stmt.setObject(1, condition.build())

        return try {
            stmt.execute()
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            conn.close()
        }
    }

    override fun query(query: String): List<SDBResultSet> {
        val conn = open()
        val stmt: Statement
        val rs: ResultSet

        try {
            stmt = conn.createStatement()
            rs = stmt.executeQuery(query)
        } catch (e : SQLException) {
            e.printStackTrace()
            return arrayListOf()
        }

        val result = ArrayList<SDBResultSet>()
        try {
            while (rs.next()){
                val meta = rs.metaData
                val data = HashMap<String,Any?>()
                for (i in 1 until meta.columnCount + 1) {
                    val name = meta.getColumnName(i)
                    val obj = rs.getObject(name)
                    if (obj == "true" || obj == "false"){
                        data[name] = obj.toString().toBoolean()
                    } else {
                        data[name] = obj
                    }
                }
                result.add(SDBResultSet(data))
            }
            return result
        } catch (e : Exception){
            e.printStackTrace()
            return arrayListOf()
        } finally {
            rs.close()
            stmt.close()
            conn.close()
        }
    }

    override fun execute(query: String): Boolean {
        val conn = open()
        val stmt = conn.createStatement()

        return try {
            stmt.execute(query)
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            conn.close()
        }
    }
}