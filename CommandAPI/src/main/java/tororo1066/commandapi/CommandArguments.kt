package tororo1066.commandapi

import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.entity.Entity

interface CommandArguments {

    fun <T> getArgument(name: String, clazz: Class<T>): T

    fun <T> getNullableArgument(name: String, clazz: Class<T>): T? {
        return try {
            getArgument(name, clazz)
        } catch (e: Exception) {
            null
        }
    }
    fun getEntities(name: String): Collection<Entity>

    fun getEnchantment(name: String): Enchantment
}