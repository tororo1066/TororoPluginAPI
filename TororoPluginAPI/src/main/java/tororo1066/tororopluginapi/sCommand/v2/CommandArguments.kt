package tororo1066.tororopluginapi.sCommand.v2

import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.entity.Entity
import tororo1066.tororopluginapi.otherUtils.UsefulUtility

interface CommandArguments {

    fun <T> getArgument(name: String, clazz: Class<T>): T

    fun <T> getNullableArgument(name: String, clazz: Class<T>): T? {
        return UsefulUtility.sTry({ getArgument(name, clazz) }, { null })
    }
    fun getEntities(name: String): Collection<Entity>

    fun getEnchantment(name: String): EnchantmentWrapper
}