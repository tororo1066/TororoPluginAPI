package tororo1066.tororoplugin

import tororo1066.tororoplugin.command.TororoCommand
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.otherClass.Dict
import java.util.*

class TororoPlugin: SJavaPlugin() {

    companion object{
        val commandLogPlayers = ArrayList<UUID>()
        val prefix = SStr("&6[&aTororo&5Plugin&cAPI&6]").toString()
        lateinit var plugin: TororoPlugin
    }

    override fun onStart() {
        TororoCommand()
        plugin = this
    }

}