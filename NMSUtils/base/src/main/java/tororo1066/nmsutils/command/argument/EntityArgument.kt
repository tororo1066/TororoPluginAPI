package tororo1066.nmsutils.command.argument

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType

class EntityArgument(val singleTarget: Boolean, val playersOnly: Boolean): ArgumentType<Any> {

    override fun parse(reader: StringReader?): Any {
        throw UnsupportedOperationException()
    }
}