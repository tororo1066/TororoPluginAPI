package tororo1066.tororopluginapi.database

import com.mongodb.client.ClientSession
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteConnection
import tororo1066.tororopluginapi.database.mongo.SMongo
import tororo1066.tororopluginapi.database.sqlite.SSQLite
import java.sql.Connection

class SSession(
    val sDatabase: SDatabase,
    val sqliteImmediateLock: Boolean = false
): AutoCloseable {
    private var sqlConnection: Connection? = null
    private var mongoSession: ClientSession? = null

    fun getSQLConnection(): Connection {
        sqlConnection?.let { return it }
        if (sDatabase.isSQL) {
            val conn = sDatabase.open() as Connection

            if (sDatabase is SSQLite && sqliteImmediateLock) {
                val sqliteConnection = conn as SQLiteConnection
                sqliteConnection.connectionConfig.transactionMode = SQLiteConfig.TransactionMode.IMMEDIATE
            }

            conn.autoCommit = false
            sqlConnection = conn
            return conn
        } else {
            throw IllegalStateException("This session is not for SQL database.")
        }
    }

    fun getMongoSession(): ClientSession {
        mongoSession?.let { return it }
        if (sDatabase.isMongo) {
            val sMongo = sDatabase as SMongo
            val session = sMongo.client.startSession().also { it.startTransaction() }
            mongoSession = session
            return session
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