package tororo1066.tororopluginapi.sItem

import net.md_5.bungee.api.ChatMessageType
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import tororo1066.tororopluginapi.SString
import tororo1066.tororopluginapi.sEvent.SEvent
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

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

    fun createSInteractItem(itemStack: ItemStack, noDump: Boolean): SInteractItem {
        return SInteractItem(this,SItem(itemStack).setCustomData(plugin,"tororopluginapi", PersistentDataType.DOUBLE,Random.nextDouble()))
    }

    init {
        SEvent(plugin).register(PlayerInteractEvent::class.java) { e ->
            if (!e.hasItem())return@register
            val item = e.item!!.clone()
            item.amount = 1
            if (!items.containsKey(item))return@register
            val interactItem = items[item]!!
            if (interactItem.interactCoolDown != 0){
                if (e.player.locale == "ja_jp"){
                    e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR,SString("&c&lCool Time&f:&e&l${ceil(interactItem.interactCoolDown.toDouble() / 2.0) / 10.0}&b&ls").toBaseComponent())
                } else {
                    e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR,SString("&c&l使用まで&f:&e&l${ceil(interactItem.interactCoolDown.toDouble() / 2.0) / 10.0}&b&l秒").toBaseComponent())
                }

                return@register
            }
            interactItem.interactEvents.forEach {
                it.accept(e,interactItem)
            }

            interactItem.interactCoolDown = interactItem.initialCoolDown

            object : BukkitRunnable() {
                override fun run() {
                    if (interactItem.interactCoolDown <= 0){
                        interactItem.interactCoolDown = 0
                        cancel()
                        return
                    }
                    interactItem.interactCoolDown--
                }
            }.runTaskTimer(plugin,0,1)
        }

        SEvent(plugin).register(PlayerDropItemEvent::class.java) { e ->
            val item = e.itemDrop.itemStack.clone()
            item.amount = 1
            if (!items.containsKey(item))return@register
            val interactItem = items[item]!!
            interactItem.dropEvents.forEach {
                it.accept(e,interactItem)
            }
        }
    }
}