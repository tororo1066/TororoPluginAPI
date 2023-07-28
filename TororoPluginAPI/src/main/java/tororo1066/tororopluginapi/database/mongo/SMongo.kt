package tororo1066.tororopluginapi.database.mongo

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.database.SDBCondition
import tororo1066.tororopluginapi.database.SDBResultSet
import tororo1066.tororopluginapi.database.SDatabase
import java.lang.NullPointerException

class SMongo: SDatabase {

    constructor(plugin: JavaPlugin): super(plugin)
    constructor(plugin: JavaPlugin, configFile: String?, configPath: String?): super(plugin, configFile, configPath)

    override fun open(): Pair<MongoClient, MongoDatabase> {
        if (host == null){
            throw NullPointerException("[MongoDB] Host name is empty.")
        }
        if (port == null){
            throw NullPointerException("[MongoDB] Port number is empty.")
        }
        if (pass == null){
            throw NullPointerException("[MongoDB] Password is empty.")
        }
        if (user == null){
            throw NullPointerException("[MongoDB] User name is empty.")
        }
        if (db == null){
            throw NullPointerException("[MongoDB] Database name is empty.")
        }

        try {
            val client = MongoClients.create("mongodb+srv://${user}:${pass}@${host}/?retryWrites=true&w=majority")
            return Pair(client, client.getDatabase(this.db))
        } catch (e: Exception) {
            throw e
        }
    }

    override fun insert(table: String, map: Map<String, Any>): Boolean {
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            collection.insertMany(map.map { Document(it.key, it.value) }).wasAcknowledged()
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

    override fun update(table: String, map: Map<String, Any>, condition: SDBCondition): Boolean {
        var client: MongoClient? = null
        return try {
            val open = open()
            client = open.first
            val db = open.second
            val collection = db.getCollection(table)
            collection.updateMany(condition.buildAsMongo(), map.map { Document(it.key, it.value) }).wasAcknowledged()
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
}