package tororo1066.tororoplugin

import org.bukkit.Bukkit
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.mysql.SMySQL
import tororo1066.tororopluginapi.mysql.ultimate.USQLTable
import tororo1066.tororopluginapi.mysql.ultimate.USQLVariable
import java.util.UUID

class TestTable: USQLTable("test", SMySQL(SJavaPlugin.plugin)){

    companion object{
        val id = USQLVariable(USQLVariable.BOOLEAN,autoIncrement = true)
        val uuid = USQLVariable(USQLVariable.VARCHAR,36,nullable = true)
        val name = USQLVariable(USQLVariable.VARCHAR,16,nullable = true)
    }

    init {
        debug = true
        createTable()
    }

    fun queryTest(searchUUID: UUID){
        select(uuid.equal(searchUUID).and().orHigher(id,5)).forEach {
            Bukkit.broadcastMessage(it.getString("uuid"))
            id.getVal(it)
        }
    }

    fun executeTest(){
        insert(arrayListOf(UUID.randomUUID()))
    }

    fun updateTest(){
        update(Pair(name,"test"),Pair(uuid,UUID.randomUUID()), condition = id.equal(1))
        update(id.equal(1),Pair(name,"test"),Pair(uuid,UUID.randomUUID()))
        update(hashMapOf(Pair(name,"test"),Pair(uuid,UUID.randomUUID())),id.equal(1))
    }
}