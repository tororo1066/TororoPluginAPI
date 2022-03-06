package tororo1066.tororopluginapi.otherUtils

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

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

    fun runTask(unit: ()->Unit){
        Bukkit.getScheduler().runTask(plugin,unit)
    }

    fun doubleToFormatString(double: Double): String {
        return UsefulUtility.doubleToFormatString(double)
    }

    fun<V> sTry(unit: ()->V,onError: (Exception)->V) : V {
        return UsefulUtility.sTry(unit,onError)
    }


}