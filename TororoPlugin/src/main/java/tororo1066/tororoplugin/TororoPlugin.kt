package tororo1066.tororoplugin

import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import java.util.UUID

class TororoPlugin: SJavaPlugin() {

    companion object{
        val commandLogPlayers = ArrayList<UUID>()
        val prefix = SStr("&6[&aTororo&5Plugin&cAPI&6]").toString()
    }

    override fun onStart() {

    }
}