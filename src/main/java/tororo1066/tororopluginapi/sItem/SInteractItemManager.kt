package tororo1066.tororopluginapi.sItem

import org.bukkit.Material
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import tororo1066.tororopluginapi.SString
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
            e.isCancelled = true
            if (interactItem.interactCoolDown != 0){
                e.player.sendRawMessage(SString("&c&l使用まで&f:&e&l${interactItem.interactCoolDown.toDouble() / 20.0}&b&l秒").toString())
                return@register
            }
            interactItem.interactEvents.forEach {
                it.accept(e)
            }

            interactItem.interactCoolDown = interactItem.initialCoolDown

            object : BukkitRunnable() {
                override fun run() {
                    if (interactItem.interactCoolDown <= 0){
                        interactItem.interactCoolDown = 0
                        cancel()
                    }
                    interactItem.interactCoolDown--
                }
            }.runTaskTimer(plugin,0,1)
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