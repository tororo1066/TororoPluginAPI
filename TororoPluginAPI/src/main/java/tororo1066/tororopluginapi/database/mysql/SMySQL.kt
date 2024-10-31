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
        logger.info("Opening MySQL connection")
        logger.config("Host: $host, Port: $port, User: $user, Database: $db")
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
            logger.info("MySQL connection opened.")
        } catch (e : SQLException){
            logger.severe("Failed to open MySQL connection.")
            throw e
        }

        return conn
    }

    override fun createTable(table: String, map: Map<String, SDBVariable<*>>): Boolean {
        logger.info("Creating table $table")
        val conn = open()
        val queryBuilder = StringBuilder()
        queryBuilder.append("create table if not exists $table (")
        queryBuilder.append(map.entries.joinToString(",") { it.key + " " + it.value.type.variableName.lowercase() + (if (it.value.length != -1) "(${it.value.length})" else "") +
                (if (!it.value.nullable) " not null" else " null") +
                (if (it.value.autoIncrement || !it.value.nullable) "" else if (it.value.default == null) " default null" else " default " + USQLCondition.modifySQLString(it.value.type,it.value.default!!)) +
                if (it.value.autoIncrement) " auto_increment" else "" })
        queryBuilder.append(if (map.entries.find { it.value.index != null } != null) ", " + map.entries.filter { it.value.index != null }.joinToString(",")
        { (if (it.value.index == SDBVariable.Index.PRIMARY) "${it.value.index!!.tableString} (${it.key})" else "${it.value.index!!.tableString} ${it.key} (${it.key})") + if (it.value.index!!.usingBTREE) " using btree" else "" } else "")
        queryBuilder.append(")")

        logger.config("Query: $queryBuilder")

        return try {
            val stmt = conn.createStatement()
            val result = stmt.execute(queryBuilder.toString())
            stmt.close()
            if (result){
                logger.info("Table $table created.")
            } else {
                logger.severe("Failed to create table $table.")
            }
            result
        } catch (e: Exception){
            logger.severe("Failed to create table $table.")
            e.printStackTrace()
            false
        } finally {
            conn.close()
        }
    }

    override fun insert(table: String, map: Map<String, Any>): Boolean {
        logger.info("Inserting data to $table")
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

        logger.config("Query: $stmt")

        return try {
            val result = stmt.execute()
            if (result){
                logger.info("Data inserted to $table.")
            } else {
                logger.severe("Failed to insert data to $table.")
            }
            result
        } catch (e: Exception){
            logger.severe("Failed to insert data to $table.")
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
        logger.info("Updating data in $table")
        val conn = open()
        val map = update as Map<String, Any>
        val query = "update $table set ${
            map.entries.joinToString(",") { "${it.key} = ?" }
        } ${condition.build()}"
        val stmt = conn.prepareStatement(query)
        map.values.forEachIndexed { index, any ->
            stmt.setObject(index+1, any)
        }

        logger.config("Query: $stmt")

        return try {
            val result = stmt.execute()
            if (result){
                logger.info("Data updated in $table.")
            } else {
                logger.severe("Failed to update data in $table.")
            }
            result
        } catch (e: Exception){
            logger.severe("Failed to update data in $table.")
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            conn.close()
        }

    }

    override fun delete(table: String, condition: SDBCondition): Boolean {
        logger.info("Deleting data from $table")
        val conn = open()
        val query = "delete from $table ?"
        val stmt = conn.prepareStatement(query)
        stmt.setString(1, condition.build())

        logger.config("Query: $stmt")

        return try {
            val result = stmt.execute()
            logger.info("Data deleted from $table.")
            result
        } catch (e: Exception){
            logger.severe("Failed to delete data from $table.")
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            conn.close()
        }
    }

    override fun query(query: String): List<SDBResultSet> {
        logger.info("Executing query: $query")
        val conn = open()
        val stmt: Statement
        val rs: ResultSet

        try {
            stmt = conn.createStatement()
            stmt.setEscapeProcessing(true)
            rs = stmt.executeQuery(query)
        } catch (e : SQLException) {
            logger.severe("Failed to execute query: $query")
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
            logger.info("Query executed successfully.")
            return result
        } catch (e : Exception){
            logger.severe("Failed to get result from query: $query")
            e.printStackTrace()
            return arrayListOf()
        } finally {
            rs.close()
            stmt.close()
            conn.close()
        }
    }

    override fun execute(query: String): Boolean {
        logger.info("Executing execute: $query")
        val conn = open()
        val stmt = conn.createStatement()

        return try {
            stmt.setEscapeProcessing(true)
            val result = stmt.execute(query)
            if (result){
                logger.info("Query executed successfully.")
            } else {
                logger.severe("Failed to execute execute: $query")
            }
            result
        } catch (e: Exception){
            logger.severe("Failed to execute execute: $query")
            e.printStackTrace()
            false
        } finally {
            stmt.close()
            conn.close()
        }
    }
}