package tororo1066.tororopluginapi.nbt

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataAdapterContext
import org.bukkit.persistence.PersistentDataType
import tororo1066.tororopluginapi.sItem.SItem

class ItemStackPersistent: PersistentDataType<String, ItemStack> {
    override fun getPrimitiveType(): Class<String> {
        return String::class.java
    }

    override fun getComplexType(): Class<ItemStack> {
        return ItemStack::class.java
    }

    override fun fromPrimitive(primitive: String, context: PersistentDataAdapterContext): ItemStack {
        return ItemStack(SItem.fromBase64(primitive)?:return ItemStack(Material.AIR))
    }

    override fun toPrimitive(complex: ItemStack, context: PersistentDataAdapterContext): String {
        return SItem(complex).toBase64()
    }

}