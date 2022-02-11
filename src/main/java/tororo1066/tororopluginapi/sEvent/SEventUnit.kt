package tororo1066.tororopluginapi.sEvent

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Consumer

class SEventUnit<T : Event>(private val eventClass: Class<T>, val plugin : JavaPlugin, private val handlers : List<Consumer<in T>>, private val finishedFunction : Consumer<SEventUnit<in T>>) : Listener, EventExecutor {

    var priority = EventPriority.NORMAL

    init {
        register()
    }

    constructor(eventClass: Class<T>, plugin: JavaPlugin, handlers : List<Consumer<in T>>, finishedFunction: Consumer<SEventUnit<in T>>, priority : EventPriority) : this(eventClass,plugin,handlers,finishedFunction) {
        this.priority = priority
    }

    private fun register(){
        Bukkit.getServer().pluginManager.registerEvent(eventClass,this,priority,this,plugin)
    }

    override fun execute(p0: Listener, p1: Event) {
        if (this.eventClass != p1.javaClass) return
        val event = this.eventClass.cast(p1)
        for (handler in handlers){
            handler.accept(event)
        }

        finishedFunction.accept(this)
    }

    /**
     * イベント解除
     * しっかり解除しとこう
     */
    fun unregister(){
        HandlerList.unregisterAll(this)
    }

}