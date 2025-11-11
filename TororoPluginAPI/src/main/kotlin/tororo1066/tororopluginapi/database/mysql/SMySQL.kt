package tororo1066.tororopluginapi.database.mysql

import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.SDBCondition
import tororo1066.tororopluginapi.database.SDBResultSet
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.database.SDatabase
import tororo1066.tororopluginapi.database.SSession
import tororo1066.tororopluginapi.mysql.ultimate.USQLCondition
import java.sql.*

class SMySQL: SDatabase {

    override val isSQL: Boolean = true
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
        } catch (e : SQLException){
            throw e
        }

        return conn
    }

    override fun createTable(table: String, map: Map<String, SDBVariable<*>>, session: SSession?): Boolean {
        val conn = session?.getSQLConnection() ?: open()
        val queryBuilder = StringBuilder()
        queryBuilder.append("create table if not exists $table (")
        queryBuilder.append(map.entries.joinToString(",") { it.key + " " + it.value.type.variableName.lowercase() + (if (it.value.length != -1) "(${it.value.length})" else "") +
                (if (!it.value.nullable) " not null" else " null") +
                (if (it.value.autoIncrement || !it.value.nullable) "" else if (it.value.default == null) " default null" else " default " + USQLCondition.modifySQLString(it.value.type,it.value.default!!)) +
                if (it.value.autoIncrement) " auto_increment" else "" })
        queryBuilder.append(if (map.entries.find { it.value.index != null } != null) ", " + map.entries.filter { it.value.index != null }.joinToString(",")
        { (if (it.value.index == SDBVariable.Index.PRIMARY) "${it.value.index!!.tableString} (${it.key})" else "${it.value.index!!.tableString} ${it.key} (${it.key})") + if (it.value.index!!.usingBTREE) " using btree" else "" } else "")
        queryBuilder.append(")")

        return try {
            val stmt = conn.createStatement()
            val result = stmt.execute(queryBuilder.toString())
            stmt.close()
            result
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            if (session == null){
                conn.close()
            }
        }
    }

    override fun insert(table: String, map: Map<String, Any?>, session: SSession?): Boolean {
        val conn = session?.getSQLConnection() ?: open()
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
            if (session == null){
                conn.close()
            }
        }
    }

    override fun select(table: String, condition: SDBCondition, session: SSession?): List<SDBResultSet> {
        val conn = session?.getSQLConnection() ?: open()
        val query = "select * from $table ${condition.build()}"
        val stmt = conn.prepareStatement(query)
        condition.processPreparedStatement(stmt)

        val rs: ResultSet
        try {
            rs = stmt.executeQuery()
        } catch (e : SQLException) {
            e.printStackTrace()
            return arrayListOf()
        }

        val result = ArrayList<SDBResultSet>()
        try {
            while (rs.next()){
                result.add(formatToSDBResultSet(rs))
            }
            return result
        } catch (e : Exception){
            e.printStackTrace()
            return arrayListOf()
        } finally {
            rs.close()
            stmt.close()
            if (session == null){
                conn.close()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun update(table: String, update: Any, condition: SDBCondition, session: SSession?): Boolean {
        val conn = session?.getSQLConnection() ?: open()
        val map = update as Map<String, Any>
        val query = "update $table set ${
            map.entries.joinToString(",") { "${it.key} = ?" }
        } ${condition.build()}"
        val stmt = conn.prepareStatement(query)
        map.values.forEachIndexed { index, any ->
            stmt.setObject(index+1, any)
        }
        condition.processPreparedStatement(stmt, map.size + 1)

        return try {
            stmt.execute()
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            if (session == null){
                conn.close()
            }
        }

    }

    override fun delete(table: String, condition: SDBCondition, session: SSession?): Boolean {
        val conn = session?.getSQLConnection() ?: open()
        val query = "delete from $table ${condition.build()}"
        val stmt = conn.prepareStatement(query)
        condition.processPreparedStatement(stmt)

        return try {
            stmt.execute()
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            if (session == null){
                conn.close()
            }
        }
    }

    override fun query(query: String, session: SSession?): List<SDBResultSet> {
        val conn = session?.getSQLConnection() ?: open()
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
                result.add(formatToSDBResultSet(rs))
            }
            return result
        } catch (e : Exception){
            e.printStackTrace()
            return arrayListOf()
        } finally {
            rs.close()
            stmt.close()
            if (session == null){
                conn.close()
            }
        }
    }

    override fun execute(query: String, session: SSession?): Boolean {
        val conn = session?.getSQLConnection() ?: open()
        val stmt = conn.createStatement()

        return try {
            stmt.execute(query)
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            if (session == null){
                conn.close()
            }
        }
    }

    private fun formatToSDBResultSet(rs: ResultSet): SDBResultSet {
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
        return SDBResultSet(data)
    }
}