package tororo1066.nmsutils.v1_20_1

import com.mojang.brigadier.Message
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.core.registries.Registries
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.network.chat.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_20_R1.CraftServer
import org.bukkit.craftbukkit.v1_20_R1.block.CraftBlock
import tororo1066.nmsutils.SNms
import tororo1066.nmsutils.command.AbstractCommandElement
import tororo1066.nmsutils.command.ArgumentCommandElement
import tororo1066.nmsutils.command.LiteralCommandElement
import tororo1066.nmsutils.command.argument.EnchantArgument
import tororo1066.nmsutils.command.argument.EntityArgument
import tororo1066.nmsutils.v1_20_1.command.CommandArgumentsImpl

class SNmsImpl: SNms {

    override fun getMapColor(block: Block): Color {
        val craftBlock = block as CraftBlock
        return Color.fromRGB(craftBlock.nms.getMapColor(craftBlock.handle,craftBlock.position).col)
    }

    override fun registerCommands(command: LiteralCommandElement, vararg commands: AbstractCommandElement<*>) {
        fun registerChildren(builder: ArgumentBuilder<CommandSourceStack, *>, element: AbstractCommandElement<*>) {
            element.children.forEach { child ->
                val converted = convert(child)

                registerChildren(converted, child)
                builder.then(converted)
            }
        }

        commands.forEach { commandElement ->
            val builder = convertToBrigadier(command)
            val argumentBuilder = convert(commandElement)

            registerChildren(argumentBuilder, commandElement)

            builder.then(argumentBuilder)

            (Bukkit.getServer() as CraftServer).server.vanillaCommandDispatcher.dispatcher.root.addChild(builder.build())
            (Bukkit.getServer() as CraftServer).server.resources.managers.commands.dispatcher.register(builder)
        }

    }

    override fun translate(text: String, vararg variable: Any): Message {
        return Component.translatable(text, *variable)
    }

    private fun convert(command: AbstractCommandElement<*>): ArgumentBuilder<CommandSourceStack, *> {
        return when(command) {
            is LiteralCommandElement -> convertToBrigadier(command)
            is ArgumentCommandElement<*> -> convertToBrigadier(convertArgumentType(command))
            else -> throw IllegalArgumentException("Unknown command element type")
        }
    }

    private fun convertToBrigadier(command: LiteralCommandElement): LiteralArgumentBuilder<CommandSourceStack> {
        return LiteralArgumentBuilder.literal<CommandSourceStack>(command.literal).apply {
            if (command.onExecute.isNotEmpty()) {
                executes { context ->
                    val sender = context.source.bukkitSender
                    command.onExecute.forEach {
                        it(sender, command.literal, CommandArgumentsImpl(context))
                    }
                    return@executes 1
                }
            }

            if (command.requirements.isNotEmpty()) {
                requires { source ->
                    val sender = source.bukkitSender
                    return@requires command.requirements.all {
                        it(sender)
                    }
                }
            }
        }
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
                    if (it.text.startsWith(arg)) suggestionsBuilder.suggest(it.text, it.toolTip)
                }

                suggestionsBuilder.buildFuture()
            }
        }
        builder.apply {
            if (command.onExecute.isNotEmpty()) {
                executes { context ->
                    val sender = context.source.bukkitSender
                    command.onExecute.forEach {
                        it(sender, command.name, CommandArgumentsImpl(context))
                    }
                    return@executes 1
                }
            }

            if (command.requirements.isNotEmpty()) {
                requires { source ->
                    val sender = source.bukkitSender
                    return@requires command.requirements.all {
                        it(sender)
                    }
                }
            }
        }
        return builder
    }

    private fun convertArgumentType(element: ArgumentCommandElement<*>): ArgumentCommandElement<*> {
        val context = Commands.createValidationContext(VanillaRegistries.createLookup())
        val newType = when(val type = element.type) {
            is EntityArgument -> when {
                type.playersOnly && type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.player()
                type.playersOnly && !type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.players()
                !type.playersOnly && type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.entity()
                else -> net.minecraft.commands.arguments.EntityArgument.entities()
            }
            is EnchantArgument -> ResourceArgument.resource(context, Registries.ENCHANTMENT)
            else -> type
        }

        return ArgumentCommandElement(element.name, newType, element.suggest).apply {
            onExecute.addAll(element.onExecute)
            children.addAll(element.children)
        }
    }
}