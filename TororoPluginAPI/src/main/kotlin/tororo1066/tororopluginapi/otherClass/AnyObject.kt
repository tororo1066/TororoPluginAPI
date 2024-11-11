package tororo1066.tororopluginapi.otherClass

import org.bukkit.Location
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.util.*

open class AnyObject(val value: Any) {

    @Suppress("UNCHECKED_CAST")
    fun <T : Any>asObj(): T {
        return value as T
    }

    inline fun <reified I: Any>instanceOf(): Boolean = value is I

    fun get(): Any = asObj()

    fun asInt(): Int = asObj()
    fun asString(): String = asObj()
    fun asDouble(): Double = asObj()
    fun asLong(): Long = asObj()
    fun asFloat(): Float = asObj()
    fun asShort(): Short = asObj()
    fun asBoolean(): Boolean = asObj()
    fun asUUID(): UUID = asObj()
    fun asLocation(): Location = asObj()
    fun asItemStack(): ItemStack = asObj()
    fun asSItem(): SItem = asObj()
    fun asSInventoryItem(): SInventoryItem = asObj()
    fun <T: Any> asList(): List<T> = asObj()
    fun <T: Any> asMutableList(): MutableList<T> = asObj()
    fun <T: Any> asArrayList(): ArrayList<T> = asObj()

}