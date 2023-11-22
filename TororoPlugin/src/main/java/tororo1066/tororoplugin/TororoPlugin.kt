package tororo1066.tororoplugin

import com.mojang.brigadier.arguments.StringArgumentType
import org.bukkit.Bukkit
import tororo1066.nmsutils.SNms
import tororo1066.nmsutils.command.*
import tororo1066.tororoplugin.command.TororoCommandV2
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
        TororoCommandV2()
        Bukkit.getScheduler().runTaskLater(this, Runnable {
//            val sNms = SNms.newInstance()
//            sNms.registerCommands("tororo", LiteralCommandElement("item")
//                .addChild(ArgumentCommandElement("args", StringArgumentType.word()) { _, _, _, builder ->
//                    builder.suggest("test")
//                    return@ArgumentCommandElement builder
//                }.onExecute { sender, label, args ->
//                    sender.sendMessage(prefix + SStr("&a${args.getArgument("args", String::class.java)}").toString())
//                }))
//            TororoCommandV2()
        }, 100)
    }

}