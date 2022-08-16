package tororo1066.tororopluginapi.otherUtils

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitTask
import java.util.Calendar
import java.util.Date
import java.util.function.Consumer

class UsefulUtility(val plugin: JavaPlugin) {

    companion object{
        fun doubleToFormatString(double: Double): String {
            return String.format("%,.0f",double)
        }

        fun<V> sTry(unit: ()->V,onError: (Exception)->V) : V {
            return try {
                unit.invoke()
            }catch (e : Exception){
                onError.invoke(e)
            }
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

    fun canDelayTask(now: Date,taskDate: Date){
        val nowCal = Calendar.getInstance().apply { time = now }
        val taskCal = Calendar.getInstance().apply { time = taskDate }

    }

}