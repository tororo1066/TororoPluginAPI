package tororo1066.tororoplugin

import tororo1066.nmsutils.SNms
import tororo1066.tororoplugin.command.TororoCommandV2
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr


class TororoPlugin: SJavaPlugin() {

    companion object{
        val prefix = SStr("&6[&aTororo&5Plugin&cAPI&6]&r")
        lateinit var plugin: TororoPlugin
        lateinit var sNms: SNms
    }

    override fun onStart() {
        saveDefaultConfig()
        plugin = this
        sNms = getSNms()
        TororoCommandV2()
    }

}