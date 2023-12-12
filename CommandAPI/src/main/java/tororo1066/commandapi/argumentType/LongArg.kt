package tororo1066.commandapi.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.LongArgumentType
import tororo1066.commandapi.SCommandV2ArgType

class LongArg(
    private val min: Long = Long.MIN_VALUE,
    private val max: Long = Long.MAX_VALUE
): SCommandV2ArgType<Long>() {

    override fun getArgumentType(): ArgumentType<*> {
        return LongArgumentType.longArg(min, max)
    }
}