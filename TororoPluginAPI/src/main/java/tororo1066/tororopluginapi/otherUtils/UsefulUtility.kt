package tororo1066.tororopluginapi.otherUtils

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.Date
import java.util.function.Consumer

class UsefulUtility(val plugin: JavaPlugin) {

    companion object{
        fun doubleToFormatString(double: Double): String {
            return String.format("%,.0f",double)
        }

        fun Double.toFormatString(): String{
            return doubleToFormatString(this)
        }

        fun<V> sTry(unit: ()->V, onError: (Exception)->V) : V {
            return try {
                unit.invoke()
            }catch (e : Exception){
                onError.invoke(e)
            }
        }

        fun<V> repeat(amount: Int, unit: ()->V): List<V> {
            val values = ArrayList<V>()
            (1..amount).forEach {
                values.add(unit.invoke())
            }
            return values
        }

    }

    fun runTask(consumer: Consumer<BukkitTask>){
        Bukkit.getScheduler().runTask(plugin,consumer)
    }

    fun doubleToFormatString(double: Double): String {
        return UsefulUtility.doubleToFormatString(double)
    }

    fun<V> sTry(unit: ()->V,onError: (Exception)->V) : V {
        return UsefulUtility.sTry(unit,onError)
    }

    fun<V> repeat(amount: Int, unit: ()->V): List<V> {
        return UsefulUtility.repeat(amount,unit)
    }

    fun<V> repeatDelay(amount: Int, delayTick: Int, repeatTick: Int, unit: ()->V) {
        var count = amount
        Bukkit.getScheduler().runTaskTimer(plugin, Consumer {
            unit.invoke()
            count--
            if (count <= 0){
                it.cancel()
                return@Consumer
            }
        },delayTick.toLong(), repeatTick.toLong())
    }

    fun<V> repeatDelay(amount: Int, repeatTick: Int, unit: ()->V) {
        repeatDelay(amount, 0, repeatTick, unit)
    }

}