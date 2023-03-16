package tororo1066.tororoplugin

import tororo1066.tororoplugin.command.TororoCommand
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import java.util.*

class TororoPlugin: SJavaPlugin() {

    companion object{
        val commandLogPlayers = ArrayList<UUID>()
        val prefix = SStr("&6[&aTororo&5Plugin&cAPI&6]").toString()
        lateinit var plugin: TororoPlugin
        lateinit var testTable: TestTable
    }

    override fun onStart() {
        TororoCommand()
        plugin = this
        testTable = TestTable()
    }

}