package tororo1066.tororopluginapi.sCommand.v2

import com.mojang.brigadier.Message
import org.bukkit.command.CommandSender
import tororo1066.nmsutils.command.AbstractCommandElement
import tororo1066.nmsutils.command.ArgumentCommandElement
import tororo1066.nmsutils.command.CommandArguments
import tororo1066.nmsutils.command.ToolTip

typealias Suggest = (sender: CommandSender, label: String, args: CommandArguments) -> Collection<ToolTip>

class SCommandV2Argument(val name: String, val type: SCommandV2ArgType<*>) : SCommandV2Arg() {

    val suggests = ArrayList<Suggest>()


    fun suggest(suggest: Suggest): SCommandV2Argument {
        suggests.add(suggest)
        return this
    }

    fun suggest(vararg suggest: ToolTip): SCommandV2Argument {
        suggests.add { _, _, _ -> suggest.toList() }
        return this
    }

    override fun toElement(): AbstractCommandElement<*> {
        return ArgumentCommandElement(name, type.getArgumentType(),
            if (suggests.isEmpty()) null else
                { sender, label, args ->
                    suggests.flatMap { it(sender, label, args) }
                }
        ).apply {
            this@SCommandV2Argument.children.forEach {
                addChild(it.toElement())
            }
            this@SCommandV2Argument.executors.forEach {  executor ->
                onExecute { sender, label, args ->
                    executor(SCommandV2Data(sender, label, args))
                }
            }

            this@SCommandV2Argument.requirements.forEach { requirement ->
                addRequirement { sender ->
                    requirement(sender)
                }
            }
        }
    }
}