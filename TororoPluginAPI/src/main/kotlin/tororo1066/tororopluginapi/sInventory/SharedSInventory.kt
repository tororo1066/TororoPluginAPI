package tororo1066.tororopluginapi.sInventory

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin


abstract class SharedSInventory(plugin : JavaPlugin, name : String, row : Int): SInventory(plugin, name, row) {

    override var savePlaceItems: Boolean
        get() = super.savePlaceItems
        set(_) {
            plugin.logger.warning("SharedSInventory not settable savePlaceItems.")
        }

    init {
        sEvent.register(InventoryCloseEvent::class.java) {
            if (!openingPlayer.contains(it.player.uniqueId))return@register
            openingPlayer.remove(it.player.uniqueId)
            if (throughEvent.remove(it.player.uniqueId)){
                return@register
            }
            for (close in onClose){
                close.accept(it)
            }
            for (close in asyncOnClose){
                thread.execute { close.accept(it) }
            }

            parent?.accept(it)
        }

        sEvent.register(InventoryClickEvent::class.java) {
            if (!openingPlayer.contains(it.whoClicked.uniqueId))return@register

            for (click in onClick){
                click.accept(it)
            }
            for (click in asyncOnClick){
                thread.execute { click.accept(it) }
            }

            items[it.rawSlot]?.active(it)

        }
    }

    @Deprecated("", ReplaceWith(""), DeprecationLevel.HIDDEN)
    override fun renderMenu(p: Player): Boolean {
        return super.renderMenu(p)
    }

    @Deprecated("", ReplaceWith(""), DeprecationLevel.HIDDEN)
    override fun afterRenderMenu(p: Player) {
        super.afterRenderMenu(p)
    }

    @Deprecated("", ReplaceWith(""), DeprecationLevel.HIDDEN)
    override fun allRenderMenu(p: Player) {
        super.allRenderMenu(p)
    }

    override fun open(p: Player) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            if (inputNow.contains(p.uniqueId)){
                p.sendMessage("§4You are entering some information.")
                return@Runnable
            }
            if (inv.viewers.isEmpty()){
                if (!renderMenu()) return@Runnable
                afterRenderMenu()
            }

            p.openInventory(inv)

            for (open in onOpen){
                open.accept(p)
            }
            for (open in asyncOnOpen){
                thread.execute { open.accept(p) }
            }

            openingPlayer.add(p.uniqueId)

        })
    }
}