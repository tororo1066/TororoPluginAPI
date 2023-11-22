package tororo1066.nmsutils.command

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import org.bukkit.command.CommandSender

class ArgumentCommandElement<T>(
    val name: String, val type: ArgumentType<T>,
    val suggest: ((sender: CommandSender, label: String, CommandArguments) -> Collection<ToolTip>)? = null
): AbstractCommandElement<T>() {

    override fun onExecute(action: (sender: CommandSender, label: String, args: CommandArguments) -> Unit): ArgumentCommandElement<T> {
        super.onExecute(action)
        return this
    }
}