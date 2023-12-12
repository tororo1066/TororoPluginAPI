package tororo1066.commandapi.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import tororo1066.commandapi.SCommandV2ArgType

class DoubleArg(
    private val min: Double = Double.MIN_VALUE,
    private val max: Double = Double.MAX_VALUE
): SCommandV2ArgType<Double>() {

    override fun getArgumentType(): ArgumentType<*> {
        return DoubleArgumentType.doubleArg(min, max)
    }
}