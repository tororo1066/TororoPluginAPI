package tororo1066.nmsutils.command

import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.entity.Entity

interface CommandArguments {
    fun <T>getArgument(name: String, clazz: Class<T>): T

    fun getEntities(name: String): Collection<Entity>

    fun getEnchantment(name: String): EnchantmentWrapper
}