package tororo1066.tororopluginapi.sCommand.v2.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import org.bukkit.command.CommandSender
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2ArgType

class DoubleArg(
    private val min: Double = Double.MIN_VALUE,
    private val max: Double = Double.MAX_VALUE
): SCommandV2ArgType<Double>() {

    override fun getArgumentType(): ArgumentType<*> {
        return DoubleArgumentType.doubleArg(min, max)
    }
}