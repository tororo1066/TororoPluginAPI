package tororo1066.tororopluginapi.sCommand

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.entity.SPlayer
import tororo1066.tororopluginapi.frombukkit.SBukkit

interface OnlyPlayerExecutor : CommandExecutor {
    fun onCommand(sender: SPlayer, command: Command, label: String, args: Array<out String>): Boolean

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        return onCommand(SBukkit.getSPlayer(p0 as Player),p1,p2,p3)
    }
}