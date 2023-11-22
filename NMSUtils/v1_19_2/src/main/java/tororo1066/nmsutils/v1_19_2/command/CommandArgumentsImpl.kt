package tororo1066.nmsutils.v1_19_2.command

import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.selector.EntitySelector
import org.bukkit.entity.Entity
import tororo1066.nmsutils.command.CommandArguments

class CommandArgumentsImpl(val commandContext: CommandContext<CommandSourceStack>): CommandArguments {

    override fun <T> getArgument(name: String, clazz: Class<T>): T {
        return commandContext.getArgument(name, clazz)
    }

    override fun getEntities(name: String): Collection<Entity> {
        return commandContext.getArgument(name, EntitySelector::class.java).findEntities(commandContext.source)
            .map { it.bukkitEntity }
    }
}