package tororo1066.tororopluginapi.event

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import tororo1066.tororopluginapi.entity.SPlayer
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem

class SInventoryClickEvent(val inventory: SInventory, val slot: Int, val player: SPlayer, val item: SInventoryItem?) : Event(), Cancellable {
    private val handler = HandlerList()

    override fun getHandlers(): HandlerList {
        return handler
    }

    fun getHandlerList(): HandlerList {
        return handler
    }

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(p0: Boolean) {
        isCancelled = p0
    }

}