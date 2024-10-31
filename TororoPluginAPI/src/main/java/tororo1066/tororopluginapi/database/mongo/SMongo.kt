package tororo1066.tororopluginapi.database.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bson.conversions.Bson
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.SDBCondition
import tororo1066.tororopluginapi.database.SDBResultSet
import tororo1066.tororopluginapi.database.SDBVariable
import tororo1066.tororopluginapi.database.SDatabase


class SMongo: SDatabase {

    override val isMongo: Boolean = true

    constructor(plugin: JavaPlugin): super(plugin)
    constructor(plugin: JavaPlugin, configFile: String?, configPath: String?): super(plugin, configFile, configPath)

    override fun open(): Pair<MongoClient, MongoDatabase> {
        logger.info("Opening MongoDB connection")
        if (url != null){
            logger.config("URL: $url, Database: $db")
        } else {
            logger.config("Host: $host, Port: $port, User: $user, Database: $db")
        }

        var url = this.url

        if (url == null){
            if (host == null){
                throw NullPointerException("[MongoDB] Host name is empty.")
            }
            if (pass == null){
                throw NullPointerException("[MongoDB] Password is empty.")
            }
            if (user == null){
                throw NullPointerException("[MongoDB] User name is empty.")
            }

            url = "mongodb://${user}:${pass}@${host}${if (port != null) ":$port" else ""}" +
                    "/?retryWrites=true&w=majority"
        }

        if (db == null){
            throw NullPointerException("[MongoDB] Database name is empty.")
        }

        try {
            val client = MongoClients.create(url)
            val pair = Pair(client, client.getDatabase(this.db))
            logger.info("MongoDB connection opened.")
            return pair
        } catch (e: Exception) {
            logger.severe("Failed to open MongoDB connection.")
            throw e
        }
    }

    override fun createTable(table: String, map: Map<String, SDBVariable<*>>): Boolean {
        logger.info("Creating collection $table")
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            db.getCollection(table)
            logger.info("Collection $table created.")
            true
        } catch (e: Exception){
            logger.severe("Failed to create collection $table.")
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }

    override fun insert(table: String, map: Map<String, Any>): Boolean {
        logger.info("Inserting data to $table")
        logger.config("Data: $map")

        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            val result = collection.insertOne(Document(map)).wasAcknowledged()
            if (result){
                logger.info("Data inserted to $table")
            } else {
                logger.severe("Failed to insert data to $table")
            }
            result
        } catch (e: Exception){
            logger.severe("Failed to insert data to $table")
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }

    override fun select(table: String, condition: SDBCondition): List<SDBResultSet> {
        logger.info("Selecting data from $table")
        logger.config("Condition: ${condition.build()}")
        var client: MongoClient? = null
        try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            val list = ArrayList<SDBResultSet>()
            collection.find(condition.buildAsMongo()).forEach {
                list.add(SDBResultSet(HashMap(it)))
            }

            return list
        } catch (e: Exception){
            logger.severe("Failed to select data from $table")
            e.printStackTrace()
            return arrayListOf()
        } finally {
            client?.close()
        }
    }

    override fun update(table: String, update: Any, condition: SDBCondition): Boolean {
        logger.info("Updating data in $table")
        logger.config("Update: $update, Condition: ${condition.build()}")
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            val result = collection.updateMany(condition.buildAsMongo(), update as Bson).wasAcknowledged()
            if (result){
                logger.info("Data updated in $table")
            } else {
                logger.severe("Failed to update data in $table")
            }
            result
        } catch (e: Exception){
            logger.severe("Failed to update data in $table")
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }

    override fun delete(table: String, condition: SDBCondition): Boolean {
        logger.info("Deleting data from $table")
        logger.config("Condition: ${condition.build()}")
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            val result = collection.deleteMany(condition.buildAsMongo()).wasAcknowledged()
            if (result){
                logger.info("Data deleted from $table")
            } else {
                logger.severe("Failed to delete data from $table")
            }
            result
        } catch (e: Exception){
            logger.severe("Failed to delete data from $table")
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }

    override fun query(query: String): List<SDBResultSet> {
        logger.info("Executing query: $query")
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val list = ArrayList<SDBResultSet>()
            db.runCommand(Document.parse(query)).forEach {
                list.add(SDBResultSet(hashMapOf(it.toPair())))
            }
            logger.info("Query executed successfully.")
            return list
        } catch (e: Exception){
            logger.severe("Failed to execute query: $query")
            e.printStackTrace()
            arrayListOf()
        } finally {
            client?.close()
        }
    }

    override fun execute(query: String): Boolean {
        logger.info("Executing command: $query")
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            db.runCommand(Document.parse(query))
            logger.info("Command executed successfully.")
            true
        } catch (e: Exception){
            logger.severe("Failed to execute command: $query")
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }
}