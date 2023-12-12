package tororo1066.commandapi.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import org.bukkit.enchantments.EnchantmentWrapper
import tororo1066.commandapi.SCommandV2ArgType
import tororo1066.commandapi.argumentType.bukkit.EnchantArgument

class EnchantArg: SCommandV2ArgType<EnchantmentWrapper>() {

    override fun getArgumentType(): ArgumentType<*> {
        return EnchantArgument()
    }
}