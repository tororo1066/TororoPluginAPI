package tororo1066.commandapi.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import tororo1066.commandapi.SCommandV2ArgType

class FloatArg(
    private val min: Float = Float.MIN_VALUE,
    private val max: Float = Float.MAX_VALUE
): SCommandV2ArgType<Float>() {

    override fun getArgumentType(): ArgumentType<*> {
        return FloatArgumentType.floatArg(min, max)
    }
}