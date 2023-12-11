package tororo1066.tororopluginapi.sCommand.v2

import com.mojang.brigadier.Message
import org.bukkit.Bukkit

class SCommandV2Object() {

    val args = ArrayList<SCommandV2Arg>()

    companion object {
        private val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")
    }

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
        val clazz = Class.forName("tororo1066.nmsutils.v${version}.SNmsImpl")
        val sNms = clazz.getConstructor().newInstance()
        args.forEach {
            command.children.add(it)
            clazz.getMethod("registerCommand", SCommandV2Literal::class.java).invoke(sNms, command)
        }
    }

    fun register() {
        val clazz = Class.forName("tororo1066.nmsutils.v${version}.SNmsImpl")
        val sNms = clazz.getConstructor().newInstance()
        args.forEach {
            clazz.getMethod("registerCommand", SCommandV2Literal::class.java).invoke(sNms, it)
        }
    }
}