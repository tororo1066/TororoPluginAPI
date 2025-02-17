package tororo1066.nmsutils.v1_20_4.command

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.commands.arguments.selector.EntitySelector
import org.bukkit.Registry
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import tororo1066.commandapi.CommandArguments

class CommandArgumentsImpl(val commandContext: CommandContext<CommandSourceStack>): CommandArguments {

    override fun <T> getArgument(name: String, clazz: Class<T>): T {
        return commandContext.getArgument(name, clazz)
    }

    override fun getEntities(name: String): Collection<Entity> {
        return try {
            commandContext.getArgument(name, EntitySelector::class.java).findEntities(commandContext.source)
                .map { it.bukkitEntity }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getEnchantment(name: String): Enchantment? {
        return try {
            val enchantment = ResourceArgument.getEnchantment(commandContext, name)
            Registry.ENCHANTMENT.get(CraftNamespacedKey.fromMinecraft(enchantment.key().location()))!!
        } catch (e: Exception) {
            null
        }
    }
}