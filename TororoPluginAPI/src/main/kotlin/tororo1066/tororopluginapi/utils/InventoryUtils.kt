package tororo1066.tororopluginapi.utils

import org.bukkit.inventory.Inventory
import tororo1066.tororopluginapi.sItem.InventoryAddable

fun Inventory.addItem(vararg items: InventoryAddable) {
    items.forEach {
        addItem(it.getItemStackForAdd())
    }
}

fun Inventory.setItem(index: Int, item: InventoryAddable) {
    setItem(index, item.getItemStackForAdd())
}