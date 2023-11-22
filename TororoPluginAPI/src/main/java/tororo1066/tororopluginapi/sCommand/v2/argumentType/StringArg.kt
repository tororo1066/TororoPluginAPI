package tororo1066.tororopluginapi.sCommand.v2.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import org.bukkit.command.CommandSender
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2ArgType

class StringArg(val type: StringType): SCommandV2ArgType<String>() {

    override fun getArgumentType(): ArgumentType<String> {
        return when(type){
            StringType.SINGLE_WORD -> StringArgumentType.word()
            StringType.QUOTABLE_PHRASE -> StringArgumentType.string()
            StringType.GREEDY_PHRASE -> StringArgumentType.greedyString()
        }
    }

    companion object {
        fun word() = StringArg(StringType.SINGLE_WORD)
        fun phrase() = StringArg(StringType.QUOTABLE_PHRASE)
        fun greedyPhrase() = StringArg(StringType.GREEDY_PHRASE)
    }

    enum class StringType {
        SINGLE_WORD,
        QUOTABLE_PHRASE,
        GREEDY_PHRASE
    }
}
