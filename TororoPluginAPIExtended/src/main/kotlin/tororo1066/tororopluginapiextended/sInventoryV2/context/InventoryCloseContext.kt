package tororo1066.tororopluginapiextended.sInventoryV2.context

import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryCloseContext(
    val inventoryCloseEvent: InventoryCloseEvent
): AbstractInventoryContext(inventoryCloseEvent) {

    val reason
        get() = inventoryCloseEvent.reason
}