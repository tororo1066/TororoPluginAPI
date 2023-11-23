package tororo1066.tororopluginapi.sCommand.v2

import tororo1066.nmsutils.SNms
import tororo1066.nmsutils.command.LiteralCommandElement
import tororo1066.nmsutils.command.ToolTip

class SCommandV2Object() {

    val args = ArrayList<SCommandV2Arg>()

    constructor(init: SCommandV2Object.() -> Unit) : this() {
        init()
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

    fun register(command: SCommandV2Literal) {
        args.forEach {
            val sNms = SNms.newInstance()
            sNms.registerCommands(command.toElement() as LiteralCommandElement, it.toElement())
        }
    }
}