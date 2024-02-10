package tororo1066.nmsutils.v1_20_1.command

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import org.bukkit.enchantments.Enchantment
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

    override fun getEnchantment(name: String): Enchantment {
        val enchantment = ResourceArgument.getEnchantment(commandContext, name)
        return EnchantmentWrapper(enchantment.key().location().path)
    }
}