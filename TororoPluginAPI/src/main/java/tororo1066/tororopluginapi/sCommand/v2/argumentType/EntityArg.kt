package tororo1066.tororopluginapi.sCommand.v2.argumentType

import com.mojang.brigadier.arguments.ArgumentType
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import tororo1066.nmsutils.command.argument.EntityArgument
import tororo1066.tororopluginapi.sCommand.v2.SCommandV2ArgType

class EntityArg(private val singleTarget: Boolean, private val playersOnly: Boolean): SCommandV2ArgType<Collection<Entity>>() {

    override fun getArgumentType(): ArgumentType<*> {
        return EntityArgument(singleTarget, playersOnly)
    }

    companion object {
        fun player() = EntityArg(singleTarget = true, playersOnly = true)
        fun players() = EntityArg(singleTarget = false, playersOnly = true)
        fun entity() = EntityArg(singleTarget = true, playersOnly = false)
        fun entities() = EntityArg(singleTarget = false, playersOnly = false)
    }
}