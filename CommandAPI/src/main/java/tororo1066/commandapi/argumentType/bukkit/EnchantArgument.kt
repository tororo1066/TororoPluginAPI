package tororo1066.commandapi.argumentType.bukkit

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType

class EnchantArgument internal constructor(): ArgumentType<Any> {
    override fun parse(reader: StringReader?): Any {
        throw UnsupportedOperationException()
    }
}