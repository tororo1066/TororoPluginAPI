package tororo1066.tororopluginapi.sEvent

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin

/**
 * イベント関連のAPI
 */
class SEvent(private val plugin : JavaPlugin) {

    constructor() : this(SJavaPlugin.plugin)

    val sEventUnits = ArrayList<SEventUnit<*>>()
    val biSEventUnits = ArrayList<BiSEventUnit<*>>()

    /**
     * イベント登録
     */
    fun <T : Event>register(clazz: Class<T> , function: (T)->Unit, finishedFunction: (SEventUnit<in T>)->Unit) : SEventUnit<T> {
        return register(clazz, EventPriority.NORMAL, listOf(function), finishedFunction)
    }

    /**
     * イベント登録
     */
    fun <T : Event>register(clazz: Class<T> , function: (T)->Unit) : SEventUnit<T> {
        return register(clazz, EventPriority.NORMAL, listOf(function)) {}
    }

    /**
     * イベント登録
     * プロパティも使えるよ
     */
    fun <T : Event>register(clazz: Class<T> , priority : EventPriority , function: List<(T)->Unit>,finishedFunction: (SEventUnit<in T>)->Unit) : SEventUnit<T> {
        val event = SEventUnit(clazz, plugin, function, finishedFunction, priority)
        sEventUnits.add(event)
        return event
    }

    /**
     * イベント登録
     * プロパティも使えるよ
     */
    fun <T : Event>register(clazz: Class<T>, priority: EventPriority, function: (T)->Unit) : SEventUnit<T> {
        return register(clazz, priority, listOf(function)) {}
    }

    fun <T : Event>biRegister(clazz: Class<T>, function: (T, BiSEventUnit<in T>)->Unit) : BiSEventUnit<T> {
        return biRegister(clazz, EventPriority.NORMAL, listOf(function))
    }

    fun <T : Event>biRegister(clazz: Class<T>, priority: EventPriority, function: (T, BiSEventUnit<in T>)->Unit) : BiSEventUnit<T> {
        return biRegister(clazz, priority, listOf(function))
    }

    fun <T : Event>biRegister(clazz: Class<T>, priority: EventPriority, function: List<(T, BiSEventUnit<in T>)->Unit>) : BiSEventUnit<T> {
        val event = BiSEventUnit(clazz,plugin,function,priority)
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
        }
        for (biSEventUnit in biSEventUnits) {
            biSEventUnit.unregister()
        }

        sEventUnits.clear()
        biSEventUnits.clear()
    }

}