package tororo1066.tororoplugin

import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.otherClass.MultipleValueMap
import java.util.*

class TororoPlugin: SJavaPlugin() {

    companion object{
        val commandLogPlayers = MultipleValueMap<UUID>()
        val prefix = SStr("&6[&aTororo&5Plugin&cAPI&6]").toString()
        lateinit var plugin: TororoPlugin
    }

    override fun onStart() {
        saveDefaultConfig()
        plugin = this
    }

}