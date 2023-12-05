package tororo1066.tororopluginapi.sCommand.v2

import org.bukkit.command.CommandSender
import tororo1066.nmsutils.command.CommandArguments

open class SCommandV2Data(open val sender: CommandSender, val label: String, val args: CommandArguments) {
}