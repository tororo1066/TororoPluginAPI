package tororo1066.tororopluginapi.database

import com.mongodb.client.ClientSession
import tororo1066.tororopluginapi.database.mongo.SMongo
import java.sql.Connection

class SSession(val sDatabase: SDatabase): AutoCloseable {
    private val sqlConnection: Connection? = null
    private val mongoSession: ClientSession? = null

    fun getSQLConnection(): Connection {
        sqlConnection?.let { return it }
        if (sDatabase.isSQL) {
            val conn = sDatabase.open() as Connection
            conn.autoCommit = false
            return conn
        } else {
            throw IllegalStateException("This session is not for SQL database.")
        }
    }

    fun getMongoSession(): ClientSession {
        mongoSession?.let { return it }
        if (sDatabase.isMongo) {
            val sMongo = sDatabase as SMongo
            return sMongo.client.startSession().also { it.startTransaction() }
        } else {
            throw IllegalStateException("This session is not for MongoDB database.")
        }
    }

    fun commit() {
        sqlConnection?.commit()
        mongoSession?.commitTransaction()
    }

    fun rollback() {
        sqlConnection?.rollback()
        mongoSession?.abortTransaction()
    }

    override fun close() {
        sqlConnection?.close()
        mongoSession?.close()
    }
}