package tororo1066.tororopluginapi.sInventory.v2.context

import org.bukkit.event.inventory.InventoryEvent

abstract class AbstractInventoryContext(
    val inventoryEvent: InventoryEvent
) {
    val player
        get() = inventoryEvent.view.player
}