package tororo1066.commandapi

import com.mojang.brigadier.arguments.ArgumentType
import org.bukkit.command.CommandSender

abstract class SCommandV2ArgType<V> {

    abstract fun getArgumentType(): ArgumentType<*>
}
