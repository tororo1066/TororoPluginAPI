package tororo1066.tororopluginapi.sCommand

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.entity.SPlayer

class SCommandData(val sender : CommandSender, val command : Command, val label : String, val args : Array<out String>)

class SCommandOnlyPlayerData(val sender : SPlayer, val command: Command, val label: String, val args: Array<out String>)