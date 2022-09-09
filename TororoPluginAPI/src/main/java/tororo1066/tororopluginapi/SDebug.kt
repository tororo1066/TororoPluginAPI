package tororo1066.tororopluginapi

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
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
    }


}