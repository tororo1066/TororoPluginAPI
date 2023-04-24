package tororo1066.tororopluginapi.sCommand

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

open class SCommandData(open val sender : CommandSender, val command : Command, val label : String, val args : Array<out String>)

class SCommandOnlyPlayerData(override val sender : Player, command: Command, label: String, args: Array<out String>): SCommandData(sender,command,label,args)