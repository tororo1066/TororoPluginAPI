package tororo1066.tororopluginapi.sCommand.v2.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import org.bukkit.command.CommandSender
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2ArgType

class IntArg(
    private val min: Int = Int.MIN_VALUE,
    private val max: Int = Int.MAX_VALUE
): SCommandV2ArgType<Int>() {

    override fun getArgumentType(): ArgumentType<*> {
        return IntegerArgumentType.integer(min, max)
    }
}