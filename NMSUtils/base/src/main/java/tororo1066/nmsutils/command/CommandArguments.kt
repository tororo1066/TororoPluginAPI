package tororo1066.nmsutils.command

import org.bukkit.entity.Entity

interface CommandArguments {
    fun <T>getArgument(name: String, clazz: Class<T>): T

    fun getEntities(name: String): Collection<Entity>
}