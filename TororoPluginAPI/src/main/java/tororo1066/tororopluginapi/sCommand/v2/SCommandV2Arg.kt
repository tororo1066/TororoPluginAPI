package tororo1066.tororopluginapi.sCommand.v2

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

typealias Requirement = (sender: CommandSender) -> Boolean
typealias Executor = (SCommandV2Data) -> Unit

abstract class SCommandV2Arg {
    val children = ArrayList<SCommandV2Arg>()
    val requirements = ArrayList<Requirement>()
    val executors = ArrayList<Executor>()

    fun literal(literal: String): SCommandV2Literal {
        return literal(literal) {}
    }

    fun literal(literal: String, init: SCommandV2Literal.() -> Unit): SCommandV2Literal {
        val element = SCommandV2Literal(literal)
        element.init()
        children.add(element)
        return element
    }

    fun argument(name: String, type: SCommandV2ArgType<*>): SCommandV2Argument {
        return argument(name, type) {}
    }

    fun argument(name: String, type: SCommandV2ArgType<*>, init: SCommandV2Argument.() -> Unit): SCommandV2Argument {
        val element = SCommandV2Argument(name, type)
        element.init()
        children.add(element)
        return element
    }

    fun arg(arg: SCommandV2Arg): SCommandV2Arg {
        children.add(arg)
        return this
    }

    fun setExecutor(executor: Executor): SCommandV2Arg {
        executors.add(executor)
        return this
    }

    fun setPlayerExecutor(executor: (data: SCommandV2PlayerData) -> Unit): SCommandV2Arg {
        return setExecutor { data ->
            val sender = data.sender as? Player ?: return@setExecutor
            executor(SCommandV2PlayerData(sender, data.label, data.args))
        }
    }

    fun setFunctionExecutor(executor: (sender: CommandSender, label: String, args: CommandArguments) -> Unit): SCommandV2Arg {
        return setExecutor { data -> executor(data.sender, data.label, data.args) }
    }

    fun setPlayerFunctionExecutor(executor: (sender: Player, label: String, args: CommandArguments) -> Unit): SCommandV2Arg {
        return setExecutor { data ->
            val sender = data.sender as? Player ?: return@setExecutor
            executor(sender, data.label, data.args)
        }
    }

    fun setRequirement(requirement: Requirement): SCommandV2Arg {
        requirements.add(requirement)
        return this
    }

    fun setPermission(permission: String): SCommandV2Arg {
        return setRequirement { sender -> sender.hasPermission(permission) }
    }
}