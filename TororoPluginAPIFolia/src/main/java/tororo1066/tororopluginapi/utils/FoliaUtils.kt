package tororo1066.tororopluginapi.utils

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin


fun foliableRunTask(plugin: JavaPlugin, func: ()->Unit){
    if (SJavaPlugin.isFolia){
        Bukkit.getGlobalRegionScheduler().run(plugin) { func.invoke() }
    } else {
        Bukkit.getScheduler().runTask(plugin, Runnable(func))
    }
}