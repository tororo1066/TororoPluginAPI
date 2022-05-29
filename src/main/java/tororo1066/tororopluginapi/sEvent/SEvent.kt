package tororo1066.tororopluginapi.sEvent

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.BiConsumer
import java.util.function.Consumer

/**
 * イベント関連のAPI
 */
class SEvent(private val plugin : JavaPlugin) {

    val sEventUnits = ArrayList<SEventUnit<*>>()
    val biSEventUnits = ArrayList<BiSEventUnit<*>>()

    /**
     * イベント登録
     */
    fun <T : Event>register(clazz: Class<T> , consumer: Consumer<T>, finishedFunction: Consumer<SEventUnit<in T>>) : SEventUnit<T> {
        return register(clazz, EventPriority.NORMAL, listOf(consumer), finishedFunction)
    }

    /**
     * イベント登録
     */
    fun <T : Event>register(clazz: Class<T> , consumer: Consumer<T>) : SEventUnit<T> {
        return register(clazz, EventPriority.NORMAL, listOf(consumer)) {}
    }

    /**
     * イベント登録
     * プロパティも使えるよ
     */
    fun <T : Event>register(clazz: Class<T> , priority : EventPriority , consumer: List<Consumer<T>>,finishedFunction: Consumer<SEventUnit<in T>>) : SEventUnit<T> {
        val event = SEventUnit(clazz,plugin, consumer,finishedFunction,priority)
        sEventUnits.add(event)
        return event
    }

    /**
     * イベント登録
     * プロパティも使えるよ
     */
    fun <T : Event>register(clazz: Class<T> , priority: EventPriority , consumer: Consumer<T>) : SEventUnit<T> {
        return register(clazz, priority, listOf(consumer)) {}
    }

    fun <T : Event>biRegister(clazz: Class<T>, consumer: BiConsumer<T,BiSEventUnit<T>>) : BiSEventUnit<T> {
        return biRegister(clazz, EventPriority.NORMAL, listOf(consumer))
    }

    fun <T : Event>biRegister(clazz: Class<T>, priority: EventPriority, consumer: BiConsumer<T,BiSEventUnit<T>>) : BiSEventUnit<T> {
        return biRegister(clazz, priority, listOf(consumer))
    }

    fun <T : Event>biRegister(clazz: Class<T>, priority: EventPriority, consumer: List<BiConsumer<T,BiSEventUnit<T>>>) : BiSEventUnit<T> {
        val event = BiSEventUnit(clazz,plugin,consumer,priority)
        biSEventUnits.add(event)
        return event
    }

    /**
     * イベント解除
     * あんまりつかわない...
     */
    fun unregister(clazz: Class<out Event>, listener: Listener){
        val method = clazz.getMethod("getHandlerList")
        val handlerList = method.invoke(null, null) as HandlerList
        handlerList.unregister(listener)
    }

    fun unregisterAll(){
        for (sEventUnit in sEventUnits) {
            sEventUnit.unregister()
            sEventUnits.remove(sEventUnit)
        }
        for (biSEventUnit in biSEventUnits) {
            biSEventUnit.unregister()
            biSEventUnits.remove(biSEventUnit)
        }
    }

}