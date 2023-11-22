package tororo1066.nmsutils.command

import org.bukkit.command.CommandSender

typealias CommandRequirements = (sender: CommandSender) -> Boolean
typealias CommandExecutor = (sender: CommandSender, label: String, args: CommandArguments) -> Unit

abstract class AbstractCommandElement<T> {

    val children = ArrayList<AbstractCommandElement<*>>()
    val requirements = ArrayList<CommandRequirements>()
    val onExecute = ArrayList<CommandExecutor>()


    open fun addChild(child: AbstractCommandElement<*>): AbstractCommandElement<T> {
        children.add(child)
        return this
    }

    open fun addRequirement(requirement: CommandRequirements): AbstractCommandElement<T> {
        requirements.add(requirement)
        return this
    }

    open fun onExecute(action: CommandExecutor): AbstractCommandElement<T> {
        onExecute.add(action)
        return this
    }
}