package tororo1066.tororopluginapi.sCommand.v2.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import org.bukkit.command.CommandSender
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2ArgType

class BooleanArg: SCommandV2ArgType<Boolean>() {

    override fun getArgumentType(): ArgumentType<*> {
        return BoolArgumentType.bool()
    }
}