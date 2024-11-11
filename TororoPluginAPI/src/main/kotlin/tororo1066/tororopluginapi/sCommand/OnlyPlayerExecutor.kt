package tororo1066.tororopluginapi.sCommand

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * プレイヤー限定のExecutor
 */
interface OnlyPlayerExecutor : CommandExecutor {
    fun onCommand(sender: Player, command: Command, label: String, args: Array<out String>): Boolean

    override fun onCommand(p0: CommandSender, p1: Command, p2: String, p3: Array<out String>): Boolean {
        return onCommand(p0 as Player,p1,p2,p3)
    }
}