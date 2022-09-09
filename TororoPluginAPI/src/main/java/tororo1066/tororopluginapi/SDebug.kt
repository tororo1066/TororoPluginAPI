package tororo1066.tororopluginapi

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.SDebug.Companion.sendDebug
import java.util.UUID

class SDebug {

    companion object{
        var consoleSenderLevel = 0
        val debugLevel = HashMap<UUID,Int>()
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
    }


}