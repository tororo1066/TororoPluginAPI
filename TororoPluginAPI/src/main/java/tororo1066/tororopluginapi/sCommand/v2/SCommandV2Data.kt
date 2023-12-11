package tororo1066.tororopluginapi.sCommand.v2

import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity

open class SCommandV2Data(open val sender: CommandSender, val label: String, val args: CommandArguments) {

    fun <T>getArgument(name: String, clazz: Class<T>): T = args.getArgument(name, clazz)

    fun getEntities(name: String): Collection<Entity> = args.getEntities(name)

    fun getEnchantment(name: String) = args.getEnchantment(name)
}