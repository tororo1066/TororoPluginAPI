package tororo1066.tororopluginapi.sItem

import org.bukkit.inventory.ItemStack

interface InventoryAddable {
    fun getItemStackForAdd(): ItemStack
}