package tororo1066.nmsutils.v1_17_1

import com.mojang.brigadier.Message
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock
import tororo1066.nmsutils.SNms
import tororo1066.nmsutils.command.AbstractCommandElement
import tororo1066.nmsutils.command.ArgumentCommandElement
import tororo1066.nmsutils.command.LiteralCommandElement
import tororo1066.nmsutils.command.argument.EntityArgument
import tororo1066.nmsutils.v1_17_1.command.CommandArgumentsImpl

class SNmsImpl: SNms {

    override fun getMapColor(block: Block): Color {
        val craftBlock = block as CraftBlock
        return Color.fromRGB(craftBlock.nms.getMapColor(craftBlock.handle,craftBlock.position).col)
    }

    override fun registerCommands(command: String, vararg commands: AbstractCommandElement<*>) {
        fun registerChildren(builder: ArgumentBuilder<CommandSourceStack, *>, element: AbstractCommandElement<*>) {
            element.children.forEach { child ->
                val converted = convert(child)

                if (child.onExecute.isNotEmpty()) {
                    converted.executes { context ->
                        val sender = context.source.bukkitSender
                        child.onExecute.forEach {
                            it(sender, command, CommandArgumentsImpl(context))
                        }
                        return@executes 1
                    }
                }

                if (child.requirements.isNotEmpty()) {
                    converted.requires { source ->
                        val sender = source.bukkitSender
                        return@requires child.requirements.all {
                            it(sender)
                        }
                    }
                }

                registerChildren(converted, child)
                builder.then(converted)
            }
        }

        commands.forEach { commandElement ->
            val builder = LiteralArgumentBuilder.literal<CommandSourceStack>(command)
            val argumentBuilder = convert(commandElement)

            if (commandElement.onExecute.isNotEmpty()) {
                commandElement.onExecute.forEach {
                    argumentBuilder.executes { context ->
                        val sender = context.source.bukkitSender
                        it(sender, command, CommandArgumentsImpl(context))
                        return@executes 1
                    }
                }
            }

            if (commandElement.requirements.isNotEmpty()) {
                commandElement.requirements.forEach {
                    argumentBuilder.requires { source ->
                        val sender = source.bukkitSender
                        return@requires it(sender)
                    }
                }
            }

            registerChildren(argumentBuilder, commandElement)

            builder.then(argumentBuilder)

            (Bukkit.getServer() as CraftServer).server.resources.commands.dispatcher.register(builder)
        }

    }

    private fun convert(command: AbstractCommandElement<*>): ArgumentBuilder<CommandSourceStack, *> {
        return when(command) {
            is LiteralCommandElement -> convertToBrigadier(command)
            is ArgumentCommandElement<*> -> convertToBrigadier(convertArgumentType(command))
            else -> throw IllegalArgumentException("Unknown command element type")
        }
    }

    private fun convertToBrigadier(command: LiteralCommandElement): LiteralArgumentBuilder<CommandSourceStack> {
        return LiteralArgumentBuilder.literal(command.literal)
    }

    private fun <T>convertToBrigadier(command: ArgumentCommandElement<T>): RequiredArgumentBuilder<CommandSourceStack, T> {
        val builder = RequiredArgumentBuilder.argument<CommandSourceStack, T>(command.name, command.type)
        if (command.suggest != null){
            builder.suggests { context, suggestionsBuilder ->

                val suggestions = command.suggest?.invoke(
                    context.source.bukkitSender,
                    context.input.split(" ")[0],
                    CommandArgumentsImpl(context)
                )
                suggestions?.forEach {
                    val arg = try {
                        context.getArgument(command.name, Any::class.java).toString()
                    } catch (e: Exception) { "" }
                    if (it.text.startsWith(arg)) suggestionsBuilder.suggest(it.text, it.toolTip?.let { Message { it } })
                }

                suggestionsBuilder.buildFuture()
            }
        }
        return builder
    }

    private fun convertArgumentType(element: ArgumentCommandElement<*>): ArgumentCommandElement<*> {
        val newType = when(val type = element.type) {
            is EntityArgument -> when {
                type.playersOnly && type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.player()
                type.playersOnly && !type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.players()
                !type.playersOnly && type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.entity()
                else -> net.minecraft.commands.arguments.EntityArgument.entities()
            }
            else -> type
        }

        return ArgumentCommandElement(element.name, newType, element.suggest).apply {
            onExecute.addAll(element.onExecute)
            children.addAll(element.children)
        }
    }
}