package tororo1066.nmsutils.command

import org.bukkit.command.CommandSender

class LiteralCommandElement(val literal: String): AbstractCommandElement<Any>() {

    override fun onExecute(action: (sender: CommandSender, label: String, args: CommandArguments) -> Unit): LiteralCommandElement {
        super.onExecute(action)
        return this
    }
}