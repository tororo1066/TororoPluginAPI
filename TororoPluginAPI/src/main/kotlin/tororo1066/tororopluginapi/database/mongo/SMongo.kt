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
            val pair = Pair(client, client.getDatabase(this.db!!))
            return pair
        } catch (e: Exception) {
            throw e
        }
    }

    override fun createTable(table: String, map: Map<String, SDBVariable<*>>): Boolean {
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            db.getCollection(table)
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }

    override fun insert(table: String, map: Map<String, Any?>): Boolean {
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            collection.insertOne(Document(map)).wasAcknowledged()
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }

    override fun select(table: String, condition: SDBCondition): List<SDBResultSet> {
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
            e.printStackTrace()
            return arrayListOf()
        } finally {
            client?.close()
        }
    }

    override fun update(table: String, update: Any, condition: SDBCondition): Boolean {
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            collection.updateMany(condition.buildAsMongo(), update as Bson).wasAcknowledged()
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }

    override fun delete(table: String, condition: SDBCondition): Boolean {
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            collection.deleteMany(condition.buildAsMongo()).wasAcknowledged()
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }

    override fun query(query: String): List<SDBResultSet> {
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val list = ArrayList<SDBResultSet>()
            db.runCommand(Document.parse(query)).forEach {
                list.add(SDBResultSet(hashMapOf(it.toPair())))
            }
            return list
        } catch (e: Exception){
            e.printStackTrace()
            arrayListOf()
        } finally {
            client?.close()
        }
    }

    override fun execute(query: String): Boolean {
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            db.runCommand(Document.parse(query))
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        } finally {
            client?.close()
        }
    }
}