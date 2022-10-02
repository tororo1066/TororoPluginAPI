package tororo1066.tororopluginapi.frombukkit

import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.annotation.SEventHandler
import tororo1066.tororopluginapi.defaultMenus.NormalInventory

class SBukkit {

    companion object{

        /**
         * SInventoryを作る
         * @param plugin メインのプラグイン
         * @param name インベントリの名前
         * @param row インベントリの大きさ(1~6)
         */
        fun createSInventory(plugin: JavaPlugin, name: String, row: Int): NormalInventory {
            return NormalInventory(plugin, name, row)
        }

        /**
         * [SEventHandler]のイベントを登録する
         *
         * [autoRegister][SEventHandler.autoRegister]がfalseの時だけ有効
         */
        fun registerSEvent(clazz: Any, plugin: JavaPlugin): Boolean{
            clazz.javaClass.methods.forEach { method ->
                if (!method.isAnnotationPresent(SEventHandler::class.java))return@forEach
                if (method.parameterTypes.size != 1)return@forEach
                val sEvent = method.getAnnotation(SEventHandler::class.java)
                val event = method.parameterTypes[0]

                val listener = object : Listener, EventExecutor {
                    override fun execute(listener: Listener, e: Event) {
                        if (e.javaClass != event)return
                        method.invoke(clazz,event.cast(e))
                    }

                }
                Bukkit.getServer().pluginManager.registerEvent(method.parameters[0].type as Class<out Event>,listener,sEvent.property,listener,plugin)
            }
            return true
        }

        /**
         * [SEventHandler]のイベントを登録する
         *
         * [autoRegister][SEventHandler.autoRegister]がfalseの時だけ有効
         */
        fun registerSEvent(clazz: Any): Boolean {
            return registerSEvent(clazz,SJavaPlugin.plugin)
        }

    }
}