package tororo1066.commandapi

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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

    override fun copy(): SCommandV2Argument {
        val copy = SCommandV2Argument(name, type)
        super.copyTo(copy)
        copy.suggests.addAll(suggests)
        return copy
    }
}