package tororo1066.tororopluginapi

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.UUID

class SDebug {

    companion object{
        var consoleSenderLevel = 0
        val debugLevel = HashMap<UUID, Int>()
        val typeDebug = HashMap<UUID, ArrayList<String>>()
        val consoleTypeDebug = ArrayList<String>()
        val debugType = ArrayList<String>(
            listOf(
                "SInteractManager",
            )
        )

        fun CommandSender.sendDebug(level: Int, msg: String){
            if (this is Player){
                val pLevel = debugLevel[this.uniqueId]?:0
                if (pLevel >= level){
                    this.sendMessage(msg)
                }
            } else {
                if (consoleSenderLevel >= level){
                    this.sendMessage(msg)
                }
            }
        }

        fun CommandSender.sendDebug(type: String, msg: String){
            if (this is Player){
                val pLevel = typeDebug[this.uniqueId]?:return
                if (pLevel.contains(type)){
                    this.sendMessage(msg)
                }
            } else {
                if (consoleTypeDebug.contains(type)){
                    this.sendMessage(msg)
                }
            }
        }

        fun broadcastDebug(level: Int, msg: String){
            Bukkit.getOnlinePlayers().forEach {
                val pLevel = debugLevel[it.uniqueId]?:0
                if (pLevel >= level){
                    it.sendMessage(msg)
                }
            }
            if (consoleSenderLevel >= level){
                Bukkit.getConsoleSender().sendMessage(msg)
            }
        }

        fun broadcastDebug(type: String, msg: String){
            Bukkit.getOnlinePlayers().forEach {
                val pLevel = typeDebug[it.uniqueId]?:return@forEach
                if (pLevel.contains(type)){
                    it.sendMessage(msg)
                }
            }
            if (consoleTypeDebug.contains(type)){
                Bukkit.getConsoleSender().sendMessage(msg)
            }
        }
    }


}