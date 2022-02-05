package tororo1066.tororopluginapi.sEvent

import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.util.function.Consumer

/**
 * イベント関連のAPI
 */
class SEvent(private val plugin : JavaPlugin) {

    /**
     * イベント登録
     */
    fun <T : Event>register(clazz: Class<T> , consumer: Consumer<T>) : SEventUnit<T> {
        return register(clazz, EventPriority.NORMAL, listOf(consumer))
    }
    /**
     * イベント登録
     * プロパティも使えるよ
     */
    fun <T : Event>register(clazz: Class<T> , priority : EventPriority , consumer: List<Consumer<T>>) : SEventUnit<T> {
        return SEventUnit(clazz,plugin, consumer,priority)
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

}