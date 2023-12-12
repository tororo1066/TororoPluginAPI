package tororo1066.nmsutils.v1_17_1.command

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ItemEnchantmentArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import net.minecraft.core.Registry
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.entity.Entity
import tororo1066.commandapi.CommandArguments

class CommandArgumentsImpl(val commandContext: CommandContext<CommandSourceStack>): CommandArguments {

    override fun <T> getArgument(name: String, clazz: Class<T>): T {
        return commandContext.getArgument(name, clazz)
    }

    override fun getEntities(name: String): Collection<Entity> {
        return commandContext.getArgument(name, EntitySelector::class.java).findEntities(commandContext.source)
            .map { it.bukkitEntity }
    }

    override fun getEnchantment(name: String): EnchantmentWrapper {
        val enchantment = ItemEnchantmentArgument.getEnchantment(commandContext, name)
        val location = Registry.ENCHANTMENT.getKey(enchantment)?.path ?: throw IllegalArgumentException("Unknown enchantment in command context($name)")
        return EnchantmentWrapper(location)
    }
}