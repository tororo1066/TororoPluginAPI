package tororo1066.tororopluginapi.sCommand

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.utils.toPlayer

open class SCommandData(open val sender : CommandSender, val command : Command, val label : String, val args : Array<out String>) {
    fun asInt(index: Int): Int {
        return args[index].toInt()
    }

    fun asDouble(index: Int): Double {
        return args[index].toDouble()
    }

    fun asPlayer(index: Int): Player {
        return args[index].toPlayer()!!
    }
}

class SCommandOnlyPlayerData(override val sender : Player, command: Command, label: String, args: Array<out String>): SCommandData(sender,command,label,args)