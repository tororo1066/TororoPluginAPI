package tororo1066.tororopluginapi.sCommand

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SCommandData(val sender : CommandSender, val command : Command, val label : String, val args : Array<out String>)

class SCommandOnlyPlayerData(val sender : Player, val command: Command, val label: String, val args: Array<out String>)