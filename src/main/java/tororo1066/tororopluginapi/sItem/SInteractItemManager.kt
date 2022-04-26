package tororo1066.tororopluginapi.sItem

import org.bukkit.Material
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.sEvent.SEvent

class SInteractItemManager(val plugin: JavaPlugin) {

    val items = HashMap<ItemStack,SInteractItem>()

    fun createSInteractItem(sItem: SItem): SInteractItem {
        return SInteractItem(this,sItem)
    }

    fun createSInteractItem(itemStack: ItemStack): SInteractItem {
        return SInteractItem(this,itemStack)
    }

    fun createSInteractItem(material: Material): SInteractItem {
        return SInteractItem(this,material)
    }

    init {
        SEvent(plugin).register(PlayerInteractEvent::class.java) { e ->
            if (!e.hasItem())return@register
            val item = e.item!!
            if (!items.containsKey(item))return@register
            val interactItem = items[item]!!
            interactItem.interactEvents.forEach {
                it.accept(e)
            }
        }

        SEvent(plugin).register(PlayerDropItemEvent::class.java) { e ->
            val item = e.itemDrop.itemStack
            if (!items.containsKey(item))return@register
            val interactItem = items[item]!!
            interactItem.dropEvents.forEach {
                it.accept(e)
            }
        }
    }
}