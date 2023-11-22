package tororo1066.tororopluginapi.sCommand.v2

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.annotation.SCommandBody

abstract class SCommandV2(val plugin: JavaPlugin, val command: String){

    val commands = ArrayList<SCommandV2Object>()

    constructor(command: String): this(SJavaPlugin.plugin, command)

    init {
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            loadAllCommands()
        }, 1)
    }

    fun addCommand(sCommandObject: SCommandV2Object){
        commands.add(sCommandObject)
    }

    protected fun command(init: SCommandV2Object.() -> Unit) = SCommandV2Object(init)

    fun loadAllCommands(){
        javaClass.declaredFields.forEach {
            if (it.isAnnotationPresent(SCommandBody::class.java) && it.type == SCommandV2Object::class.java){
                it.isAccessible = true
                val data = it.get(this) as SCommandV2Object
                addCommand(data)
                data.register(command)
            }
        }

        Bukkit.getOnlinePlayers().forEach {
            it.updateCommands()
        }
    }
}