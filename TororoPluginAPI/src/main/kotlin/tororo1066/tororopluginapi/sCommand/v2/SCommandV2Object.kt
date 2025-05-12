package tororo1066.tororopluginapi.sCommand.v2

import com.mojang.brigadier.Message
import tororo1066.commandapi.*
import tororo1066.tororopluginapi.SJavaPlugin

class SCommandV2Object() {

    val args = ArrayList<SCommandV2Arg>()
    val requirements = ArrayList<Requirement>()

    constructor(init: SCommandV2Object.() -> Unit) : this() {
        init.invoke(this)
    }

    fun addArg(arg: SCommandV2Arg): SCommandV2Object {
        args.add(arg)
        return this
    }

    fun literal(vararg literal: String, init: SCommandV2Literal.() -> Unit): SCommandV2Literal {
        return SCommandV2Literal(*literal).apply(init).also { addArg(it) }
    }

    fun argument(name: String, type: SCommandV2ArgType<*>, init: SCommandV2Argument.() -> Unit): SCommandV2Argument {
        return SCommandV2Argument(name, type).apply(init).also { addArg(it) }
    }

    infix fun String.toolTip(toolTip: String) = ToolTip(this, toolTip)

    infix fun String.toolTip(toolTip: Message?) = ToolTip(this, toolTip)

    fun setRequirement(requirement: Requirement): SCommandV2Object {
        requirements.add(requirement)
        return this
    }

    fun setPermission(permission: String): SCommandV2Object {
        requirements.add {
            it.hasPermission(permission)
        }
        return this
    }

    fun register(command: SCommandV2Literal) {
        val sNms = SJavaPlugin.getSNms()
        args.forEach {
            command.children.add(it)
            sNms.registerCommand(command.copy().also { arg ->
                arg.requirements.addAll(requirements)
            })
        }
    }

    fun register() {
        val sNms = SJavaPlugin.getSNms()
        args.forEach {
            sNms.registerCommand((it as SCommandV2Literal).copy().also { arg ->
                arg.requirements.addAll(requirements)
            })
        }
    }
}