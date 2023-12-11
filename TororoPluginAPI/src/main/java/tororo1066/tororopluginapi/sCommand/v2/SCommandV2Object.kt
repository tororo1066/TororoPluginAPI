package tororo1066.tororopluginapi.sCommand.v2

import com.mojang.brigadier.Message
import org.bukkit.Bukkit
import tororo1066.tororopluginapi.nms.SNms

class SCommandV2Object() {

    val args = ArrayList<SCommandV2Arg>()

    constructor(init: SCommandV2Object.() -> Unit) : this() {
        init.invoke(this)
    }

    fun addArg(arg: SCommandV2Arg): SCommandV2Object {
        args.add(arg)
        return this
    }

    fun literal(literal: String, init: SCommandV2Literal.() -> Unit): SCommandV2Literal {
        return SCommandV2Literal(literal).apply(init).also { addArg(it) }
    }

    fun argument(name: String, type: SCommandV2ArgType<*>, init: SCommandV2Argument.() -> Unit): SCommandV2Argument {
        return SCommandV2Argument(name, type).apply(init).also { addArg(it) }
    }

    infix fun String.toolTip(toolTip: String) = ToolTip(this, toolTip)

    infix fun String.toolTip(toolTip: Message?) = ToolTip(this, toolTip)

    fun register(command: SCommandV2Literal) {
        val sNms = SNms.newInstance()
        args.forEach {
            command.children.add(it)
            sNms.registerCommand(command)
        }
    }

    fun register() {
        val sNms = SNms.newInstance()
        args.forEach {
            sNms.registerCommand(it as SCommandV2Literal)
        }
    }
}