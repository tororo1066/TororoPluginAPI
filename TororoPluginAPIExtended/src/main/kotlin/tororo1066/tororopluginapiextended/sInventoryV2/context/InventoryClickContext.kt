package tororo1066.tororopluginapiextended.sInventoryV2.context

import org.bukkit.event.inventory.InventoryClickEvent
import tororo1066.tororopluginapiextended.sInventoryV2.itemStack.ModernItemStack

class InventoryClickContext(
    val inventoryClickEvent: InventoryClickEvent,
    val modernItemStack: ModernItemStack
): AbstractInventoryContext(inventoryClickEvent) {

}