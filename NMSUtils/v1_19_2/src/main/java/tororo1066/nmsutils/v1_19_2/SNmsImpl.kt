package tororo1066.nmsutils.v1_19_2

import com.mojang.brigadier.Message
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.ResourceKeyArgument
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_19_R1.CraftServer
import org.bukkit.craftbukkit.v1_19_R1.block.CraftBlock
import tororo1066.nmsutils.SNms_A
import tororo1066.nmsutils.v1_19_2.command.CommandArgumentsImpl
import tororo1066.tororopluginapi.nms.SNms
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2Arg
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2Argument
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2Data
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2Literal
import tororo1066.tororopluginapi.sCommand.v2.argumentType.bukkit.EnchantArgument
import tororo1066.tororopluginapi.sCommand.v2.argumentType.bukkit.EntityArgument

class SNmsImpl: SNms {

    override fun getMapColor(block: Block): Color {
        val craftBlock = block as CraftBlock
        return Color.fromRGB(craftBlock.nms.getMapColor(craftBlock.handle,craftBlock.position).col)
    }

    override fun registerCommand(command: SCommandV2Literal) {
        val server = (Bukkit.getServer() as CraftServer).server
        val converted = convertToBrigadier(command)
        server.vanillaCommandDispatcher.dispatcher.root.addChild(converted.build())
        server.resources.managers.commands.dispatcher.register(converted)
    }

    private fun convert(command: SCommandV2Arg): ArgumentBuilder<CommandSourceStack, *> {
        return when(command) {
            is SCommandV2Literal -> convertToBrigadier(command)
            is SCommandV2Argument -> convertToBrigadier(command, convertArgumentType(command))
            else -> throw IllegalArgumentException("Unknown command element type")
        }
    }

    private fun convertToBrigadier(command: SCommandV2Literal): LiteralArgumentBuilder<CommandSourceStack> {
        return LiteralArgumentBuilder.literal<CommandSourceStack>(command.literal).apply {
            applyCommand(command, this)
        }
    }

    private fun <T>convertToBrigadier(command: SCommandV2Argument, argumentType: ArgumentType<T>): RequiredArgumentBuilder<CommandSourceStack, T> {
        val builder = RequiredArgumentBuilder.argument<CommandSourceStack, T>(command.name, argumentType).apply {
            if (command.suggests.isNotEmpty()) {
                suggests { context, suggestionsBuilder ->
                    val suggestions = command.suggests.flatMap { it(SCommandV2Data(context.source.bukkitSender, context.input.split(" ")[0], CommandArgumentsImpl(context))) }
                    suggestions.forEach {
                        val arg = try {
                            context.getArgument(command.name, Any::class.java).toString()
                        } catch (e: Exception) { "" }
                        if (it.text.startsWith(arg)) suggestionsBuilder.suggest(it.text, it.toolTip)
                    }
                    suggestionsBuilder.buildFuture()
                }
            }
        }
        builder.apply {
            applyCommand(command, this)
        }
        return builder
    }

    private fun convertArgumentType(element: SCommandV2Argument): ArgumentType<*> {
        val newType = when(val type = element.type.getArgumentType()) {
            is EntityArgument -> when {
                type.playersOnly && type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.player()
                type.playersOnly && !type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.players()
                !type.playersOnly && type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.entity()
                else -> net.minecraft.commands.arguments.EntityArgument.entities()
            }
            is EnchantArgument -> ResourceKeyArgument.key(Registry.ENCHANTMENT_REGISTRY)
            else -> type
        }

        return newType
    }

    private fun applyCommand(command: SCommandV2Arg, builder: ArgumentBuilder<CommandSourceStack, *>) {
        fun registerChildren(builder: ArgumentBuilder<CommandSourceStack, *>, element: SCommandV2Arg) {
            element.children.forEach { child ->
                val converted = convert(child)

                registerChildren(converted, child)
                builder.then(converted)
            }
        }

        if (command.executors.isNotEmpty()) {
            builder.executes { context ->
                val sender = context.source.entity?.getBukkitSender(context.source)?: context.source.bukkitSender
                command.executors.forEach {
                    it(SCommandV2Data(sender, context.input.split(" ")[0], CommandArgumentsImpl(context)))
                }
                return@executes 1
            }
        }

        if (command.requirements.isNotEmpty()) {
            builder.requires { source ->
                val sender = source.entity?.getBukkitSender(source)?: source.bukkitSender
                return@requires command.requirements.all {
                    it(sender)
                }
            }
        }

        if (command.children.isNotEmpty()) {
            registerChildren(builder, command)
        }
    }

    override fun translate(text: String, vararg variable: Any): Message {
        return Component.translatable(text, *variable)
    }
}