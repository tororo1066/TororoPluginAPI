package tororo1066.nmsutils.v1_21_1

import com.mojang.brigadier.Message
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceArgument
import net.minecraft.core.registries.Registries
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.network.chat.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.block.Block
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.block.CraftBlock
import tororo1066.commandapi.SCommandV2Arg
import tororo1066.commandapi.SCommandV2Argument
import tororo1066.commandapi.SCommandV2Data
import tororo1066.commandapi.SCommandV2Literal
import tororo1066.commandapi.argumentType.bukkit.EnchantArgument
import tororo1066.commandapi.argumentType.bukkit.EntityArgument
import tororo1066.nmsutils.SNms
import tororo1066.nmsutils.v1_21_1.command.CommandArgumentsImpl

class SNmsImpl: SNms {

    override fun getMapColor(block: Block): Color {
        val craftBlock = block as CraftBlock
        return Color.fromRGB(craftBlock.nms.getMapColor(craftBlock.handle,craftBlock.position).col)
    }

    override fun registerCommand(command: SCommandV2Literal) {
        val craftServer = Bukkit.getServer() as CraftServer
        val server = craftServer.server
        val converted = convertToBrigadier(command)
        converted.forEach {
            server.commands.dispatcher.register(it)
        }
    }

    private fun convert(command: SCommandV2Arg): List<ArgumentBuilder<CommandSourceStack, *>> {
        return when(command) {
            is SCommandV2Literal -> convertToBrigadier(command)
            is SCommandV2Argument -> convertToBrigadier(command, convertArgumentType(command))
            else -> throw IllegalArgumentException("Unknown command element type")
        }
    }

    private fun convertToBrigadier(command: SCommandV2Literal): List<LiteralArgumentBuilder<CommandSourceStack>> {
        val commands = ArrayList<LiteralArgumentBuilder<CommandSourceStack>>()
        command.literal.forEach { literal ->
            val builder = LiteralArgumentBuilder.literal<CommandSourceStack>(literal).apply {
                applyCommand(command, this)
            }
            commands.add(builder)
        }

        return commands
    }

    private fun <T>convertToBrigadier(command: SCommandV2Argument, argumentType: ArgumentType<T>): List<RequiredArgumentBuilder<CommandSourceStack, T>> {
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
        return listOf(builder)
    }

    private val context: CommandBuildContext = Commands.createValidationContext(VanillaRegistries.createLookup())
    private fun convertArgumentType(element: SCommandV2Argument): ArgumentType<*> {
        val newType: ArgumentType<*> = when(val type = element.type.getArgumentType()) {
            is EntityArgument -> when {
                type.playersOnly && type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.player()
                type.playersOnly && !type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.players()
                !type.playersOnly && type.singleTarget -> net.minecraft.commands.arguments.EntityArgument.entity()
                else -> net.minecraft.commands.arguments.EntityArgument.entities()
            }
            is EnchantArgument -> ResourceArgument.resource(context, Registries.ENCHANTMENT)
            else -> type
        }

        return newType
    }

    private fun applyCommand(command: SCommandV2Arg, builder: ArgumentBuilder<CommandSourceStack, *>) {
        fun registerChildren(builder: ArgumentBuilder<CommandSourceStack, *>, element: SCommandV2Arg) {
            element.children.forEach { child ->
                val converted = convert(child)

                converted.forEach {
                    registerChildren(it, child)
                    builder.then(it)
                }
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