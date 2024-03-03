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
class SEvent(val plugin : JavaPlugin) {

    constructor() : this(SJavaPlugin.plugin)

    val sEventUnits = ArrayList<SEventUnit<*>>()
    val biSEventUnits = ArrayList<BiSEventUnit<*>>()

    /**
     * イベント登録
     */
    fun <T : Event>register(clazz: Class<T>, function: (e: T) -> Unit) : SEventUnit<T> {
        return register(clazz, EventPriority.NORMAL, listOf(function))
    }

    /**
     * イベント登録
     * プロパティも使えるよ
     */
    fun <T : Event>register(clazz: Class<T>, priority: EventPriority, function: List<(e: T) -> Unit>) : SEventUnit<T> {
        val event = SEventUnit(clazz, plugin, function, priority)
        sEventUnits.add(event)
        return event
    }

    inline fun <reified T : Event>register(noinline function: (e: T)->Unit) : SEventUnit<T> {
        return register(T::class.java, EventPriority.NORMAL, listOf(function))
    }

    /**
     * イベント登録
     * プロパティも使えるよ
     */
    fun <T : Event>register(clazz: Class<T>, priority: EventPriority, function: (e: T)->Unit) : SEventUnit<T> {
        return register(clazz, priority, listOf(function))
    }

    fun <T : Event>biRegister(clazz: Class<T>, function: (e: T, unit: BiSEventUnit<in T>)->Unit) : BiSEventUnit<T> {
        return biRegister(clazz, EventPriority.NORMAL, listOf(function))
    }

    fun <T : Event>biRegister(clazz: Class<T>, priority: EventPriority, function: (e: T, unit: BiSEventUnit<in T>)->Unit) : BiSEventUnit<T> {
        return biRegister(clazz, priority, listOf(function))
    }

    fun <T : Event>biRegister(clazz: Class<T>, priority: EventPriority, function: List<(e: T, unit: BiSEventUnit<in T>)->Unit>) : BiSEventUnit<T> {
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