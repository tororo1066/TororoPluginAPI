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
import tororo1066.tororopluginapi.database.SSession


class SMongo: SDatabase {

    override val isSQL: Boolean = false
    override val isMongo: Boolean = true

    val client: MongoClient by lazy {
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

        try {
            MongoClients.create(url)
        } catch (e: Exception) {
            throw e
        }
    }

    constructor(plugin: JavaPlugin): super(plugin)
    constructor(plugin: JavaPlugin, configFile: String?, configPath: String?): super(plugin, configFile, configPath)

    override fun open(): MongoDatabase {
        val db = this.db ?: throw NullPointerException("[MongoDB] Database name is empty.")

        try {
            return client.getDatabase(db)
        } catch (e: Exception) {
            throw e
        }
    }

    override fun createTable(table: String, map: Map<String, SDBVariable<*>>, session: SSession?): Boolean {
        return try {
            val db = open()
            if (session != null) {
                db.createCollection(session.getMongoSession(), table)
            } else {
                db.createCollection(table)
            }
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    override fun insert(table: String, map: Map<String, Any?>, session: SSession?): Boolean {
        return try {
            val db = open()
            val collection = db.getCollection(table)
            if (session != null) {
                collection.insertOne(session.getMongoSession(), Document(map))
            } else {
                collection.insertOne(Document(map))
            }
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    override fun select(table: String, condition: SDBCondition, session: SSession?): List<SDBResultSet> {
        try {
            val db = open()
            val collection = db.getCollection(table)
            val list = ArrayList<SDBResultSet>()
            val findIterable = if (session != null) {
                collection.find(session.getMongoSession(), condition.buildAsMongo())
            } else {
                collection.find(condition.buildAsMongo())
            }
            findIterable.forEach {
                list.add(SDBResultSet(HashMap(it)))
            }

            return list
        } catch (e: Exception){
            e.printStackTrace()
            return arrayListOf()
        }
    }

    override fun update(table: String, update: Any, condition: SDBCondition, session: SSession?): Boolean {
        return try {
            val db = open()
            val collection = db.getCollection(table)
            val updateBson = update as Bson
            if (session != null) {
                collection.updateMany(session.getMongoSession(), condition.buildAsMongo(), updateBson).wasAcknowledged()
            } else {
                collection.updateMany(condition.buildAsMongo(), updateBson).wasAcknowledged()
            }
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    override fun delete(table: String, condition: SDBCondition, session: SSession?): Boolean {
        return try {
            val db = open()
            val collection = db.getCollection(table)
            if (session != null) {
                collection.deleteMany(session.getMongoSession(), condition.buildAsMongo()).wasAcknowledged()
            } else {
                collection.deleteMany(condition.buildAsMongo()).wasAcknowledged()
            }
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    override fun query(query: String, session: SSession?): List<SDBResultSet> {
        return try {
            val db = open()
            val list = ArrayList<SDBResultSet>()
            val result = if (session != null) {
                db.runCommand(session.getMongoSession(), Document.parse(query))
            } else {
                db.runCommand(Document.parse(query))
            }
            result.forEach {
                list.add(SDBResultSet(hashMapOf(it.toPair())))
            }
            return list
        } catch (e: Exception){
            e.printStackTrace()
            arrayListOf()
        }
    }

    override fun execute(query: String, session: SSession?): Boolean {
        return try {
            val db = open()
            if (session != null) {
                db.runCommand(session.getMongoSession(), Document.parse(query))
            } else {
                db.runCommand(Document.parse(query))
            }
            true
        } catch (e: Exception){
            e.printStackTrace()
            false
        }
    }

    override fun close() {
        client.close()
    }
}