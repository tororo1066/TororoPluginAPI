package tororo1066.tororopluginapi.sEvent

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.BiConsumer

class BiSEventUnit<T : Event>(private val eventClass: Class<T>, val plugin : JavaPlugin, private val handlers : List<BiConsumer<in T,BiSEventUnit<T>>>) : Listener, EventExecutor {

    var priority = EventPriority.NORMAL

    init {
        register()
    }

    constructor(eventClass: Class<T>, plugin: JavaPlugin, handlers : List<BiConsumer<in T,BiSEventUnit<T>>>, priority : EventPriority) : this(eventClass,plugin,handlers) {
        this.priority = priority
    }

    private fun register(){
        Bukkit.getServer().pluginManager.registerEvent(eventClass,this,priority,this,plugin)
    }

    override fun execute(p0: Listener, p1: Event) {
        if (this.eventClass != p1.javaClass) return
        val event = this.eventClass.cast(p1)
        for (handler in handlers){
            handler.accept(event,this)
        }
    }

    /**
     * イベント解除
     * しっかり解除しとこう
     */
    fun unregister(){
        HandlerList.unregisterAll(this)
    }

}