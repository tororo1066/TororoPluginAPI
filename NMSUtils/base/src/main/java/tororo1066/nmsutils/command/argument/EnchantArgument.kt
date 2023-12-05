package tororo1066.nmsutils.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType

class EnchantArgument: ArgumentType<Any> {

    override fun parse(reader: StringReader?): Any {
        throw UnsupportedOperationException()
    }
}