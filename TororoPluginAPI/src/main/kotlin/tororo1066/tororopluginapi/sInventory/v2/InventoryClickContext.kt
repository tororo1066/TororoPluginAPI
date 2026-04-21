package tororo1066.tororopluginapi.sInventory.v2

import org.bukkit.event.inventory.InventoryClickEvent

class InventoryClickContext(
    val inventoryClickEvent: InventoryClickEvent,
    val modernItemStack: ModernItemStack
) {
    val player
        get() = inventoryClickEvent.whoClicked
}