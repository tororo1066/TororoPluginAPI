package tororo1066.tororopluginapi.sCommand.v2.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentWrapper
import tororo1066.nmsutils.command.argument.EnchantArgument
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2ArgType

class EnchantArg: SCommandV2ArgType<EnchantmentWrapper>() {

    override fun getArgumentType(): ArgumentType<*> {
        return EnchantArgument()
    }
}