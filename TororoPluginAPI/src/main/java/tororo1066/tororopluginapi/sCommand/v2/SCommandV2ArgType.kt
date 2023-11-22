package tororo1066.tororopluginapi.sCommand.v2

import com.mojang.brigadier.arguments.ArgumentType
import org.bukkit.command.CommandSender

abstract class SCommandV2ArgType<V> {

    abstract fun getArgumentType(): ArgumentType<*>
}
