package tororo1066.tororopluginapi.sCommand.v2

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.annotation.SCommandV2Body

abstract class SCommandV2(val plugin: JavaPlugin, val command: String, val permission: String? = null) {

    val commands = ArrayList<SCommandV2Object>()
    protected val root = SCommandV2Literal(command)

    constructor(command: String): this(SJavaPlugin.plugin, command)

    constructor(command: String, permission: String): this(SJavaPlugin.plugin, command, permission)

    init {
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, Runnable {
            loadAllCommands()
        }, 50)
    }

    fun addCommand(sCommandObject: SCommandV2Object){
        commands.add(sCommandObject)
    }

    protected fun command(init: SCommandV2Object.() -> Unit) = SCommandV2Object(init)

    fun loadAllCommands(){
        javaClass.declaredFields.forEach {
            if (it.isAnnotationPresent(SCommandV2Body::class.java) && it.type == SCommandV2Object::class.java){
                it.isAccessible = true
                val data = it.get(this) as SCommandV2Object
                val annotation = it.getAnnotation(SCommandV2Body::class.java)
                addCommand(data)
                if (annotation.asRoot){
                    data.register()
                } else {
                    data.register(root)
                }
            }
        }

        Bukkit.getOnlinePlayers().forEach {
            it.updateCommands()
        }
    }
}