package tororo1066.tororoplugin

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import tororo1066.nmsutils.SNms
import tororo1066.tororoplugin.command.TororoCommand
import tororo1066.tororoplugin.command.TororoCommandV2
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.database.SDatabase
import tororo1066.tororopluginapi.otherClass.MultipleValueMap
import java.util.*


class TororoPlugin: SJavaPlugin() {

    companion object{
        val commandLogPlayers = MultipleValueMap<UUID>()
        val prefix = SStr("&6[&aTororo&5Plugin&cAPI&6]&r")
        val prefixString = SStr("&6[&aTororo&5Plugin&cAPI&6]").toString()
        lateinit var plugin: TororoPlugin
        lateinit var sNms: SNms
    }

    override fun onStart() {
        saveDefaultConfig()
        plugin = this
        sNms = SNms.newInstance()
        TororoCommandV2()
    }

}