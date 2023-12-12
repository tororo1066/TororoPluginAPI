package tororo1066.commandapi.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import tororo1066.commandapi.SCommandV2ArgType

class BooleanArg: SCommandV2ArgType<Boolean>() {

    override fun getArgumentType(): ArgumentType<*> {
        return BoolArgumentType.bool()
    }
}