package tororo1066.tororopluginapi.sCommand.v2.argumentType.bukkit

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType

class EnchantArgument: ArgumentType<Any> {
    override fun parse(reader: StringReader?): Any {
        throw UnsupportedOperationException()
    }
}