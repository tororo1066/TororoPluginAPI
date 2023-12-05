package tororo1066.tororopluginapi.sCommand.v2

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tororo1066.nmsutils.command.AbstractCommandElement
import tororo1066.nmsutils.command.ArgumentCommandElement
import tororo1066.nmsutils.command.CommandArguments
import tororo1066.nmsutils.command.ToolTip

typealias Suggest = (SCommandV2Data) -> Collection<ToolTip>

class SCommandV2Argument(val name: String, val type: SCommandV2ArgType<*>) : SCommandV2Arg() {

    val suggests = ArrayList<Suggest>()


    fun suggest(suggest: Suggest): SCommandV2Argument {
        suggests.add(suggest)
        return this
    }

    fun suggest(suggest: (sender: CommandSender, label: String, args: CommandArguments) -> Collection<ToolTip>): SCommandV2Argument {
        suggests.add { data -> suggest(data.sender, data.label, data.args) }
        return this
    }

    fun suggest(vararg suggest: ToolTip): SCommandV2Argument {
        suggests.add { suggest.toList() }
        return this
    }

    fun playerSuggest(suggest: (data: SCommandV2PlayerData) -> Collection<ToolTip>): SCommandV2Argument {
        return suggest { data ->
            val sender = data.sender as? Player ?: return@suggest emptyList()
            suggest(SCommandV2PlayerData(sender, data.label, data.args))
        }
    }

    fun playerSuggest(suggest: (sender: Player, label: String, args: CommandArguments) -> Collection<ToolTip>): SCommandV2Argument {
        return suggest { data ->
            val sender = data.sender as? Player ?: return@suggest emptyList()
            suggest(sender, data.label, data.args)
        }
    }

    override fun toElement(): AbstractCommandElement<*> {
        return ArgumentCommandElement(name, type.getArgumentType(),
            if (suggests.isEmpty()) null else
                { sender, label, args ->
                    suggests.flatMap { it(SCommandV2Data(sender, label, args)) }
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