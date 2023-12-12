package tororo1066.commandapi.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import tororo1066.commandapi.SCommandV2ArgType

class IntArg(
    private val min: Int = Int.MIN_VALUE,
    private val max: Int = Int.MAX_VALUE
): SCommandV2ArgType<Int>() {

    override fun getArgumentType(): ArgumentType<*> {
        return IntegerArgumentType.integer(min, max)
    }
}