package tororo1066.tororopluginapi.database.mysql

import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.SDBCondition
import tororo1066.tororopluginapi.database.SDBResultSet
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.database.SDatabase
import tororo1066.tororopluginapi.mysql.ultimate.USQLCondition
import java.sql.*

class SMySQL: SDatabase {

    override val isMongo: Boolean = false

    constructor(plugin: JavaPlugin): super(plugin)
    constructor(plugin: JavaPlugin, configFile: String?, configPath: String?): super(plugin, configFile, configPath)

    override fun open(): Connection {
        val conn: Connection
        try {
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
        }catch (e : SQLException){
            throw e
        }

        return conn
    }

    override fun createTable(table: String, map: Map<String, SDBVariable<*>>): Boolean {
        val conn = open()
        val queryBuilder = StringBuilder()
        queryBuilder.append("create table if not exists $table (")
        queryBuilder.append(map.values.joinToString(",") { it.name + " " + it.type.variableName.lowercase() + (if (it.length != -1) "(${it.length})" else "") +
                (if (!it.nullable) " not null" else " null") +
                (if (it.autoIncrement || !it.nullable) "" else if (it.default == null) " default null" else " default " + USQLCondition.modifySQLString(it.type,it.default!!)) +
                if (it.autoIncrement) " auto_increment" else "" })
        queryBuilder.append(if (map.values.find { it.index != null } != null) ", " + map.values.filter { it.index != null }.joinToString(",")
        { (if (it.index == SDBVariable.Index.PRIMARY) "${it.index!!.tableString} (${it.name})" else "${it.index!!.tableString} ${it.name} (${it.name})") + if (it.index!!.usingBTREE) " using btree" else "" } else "")
        queryBuilder.append(")")

        return try {
            val stmt = conn.createStatement()
            stmt.execute(queryBuilder.toString())
            stmt.close()
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            conn.close()
        }
    }

    override fun insert(table: String, map: Map<String, Any>): Boolean {
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
        val conn = open()
        val stmt: Statement
        val rs: ResultSet

        try {
            stmt = conn.createStatement()
            rs = stmt.executeQuery("select * from $table ${condition.build()}")
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

    @Suppress("UNCHECKED_CAST")
    override fun update(table: String, update: Any, condition: SDBCondition): Boolean {
        val conn = open()
        val map = update as Map<String, Any>
        val query = "update $table set ${map.entries.joinToString(",") { "${it.key} = ?" }}"
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

    override fun delete(table: String, condition: SDBCondition): Boolean {
        val conn = open()
        val query = "delete from $table ${condition.build()}"
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