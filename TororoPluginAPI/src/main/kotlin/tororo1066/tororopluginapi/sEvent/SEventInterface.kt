package tororo1066.tororopluginapi.sEvent

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin

abstract class SEventInterface<T : Event>(val plugin : JavaPlugin, val event : Class<T>) : Listener, EventExecutor {

    constructor(event: Class<T>): this(SJavaPlugin.plugin,event)

    private var priority = EventPriority.NORMAL

    init {
        register()
    }

    constructor(plugin : JavaPlugin, event : Class<T>, priority: EventPriority) : this(plugin, event){
        this.priority = priority
    }

    constructor(event : Class<T>, priority: EventPriority) : this(SJavaPlugin.plugin, event){
        this.priority = priority
    }

    private fun register(){
        Bukkit.getServer().pluginManager.registerEvent(event,this,priority,this,plugin)
    }

    abstract fun executeEvent(e : T)

    override fun execute(p0: Listener, p1: Event) {
        if (event != p1.javaClass)return
        val cast = event.cast(p1)
        executeEvent(cast)
    }
}