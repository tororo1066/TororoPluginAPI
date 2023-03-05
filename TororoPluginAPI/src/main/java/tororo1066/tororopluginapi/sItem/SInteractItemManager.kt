package tororo1066.tororopluginapi.sItem

import net.md_5.bungee.api.ChatMessageType
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sEvent.SEvent
import kotlin.math.ceil
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
        return if (noDump){
            SInteractItem(this,SItem(itemStack).setCustomData(plugin,"tororopluginapi", PersistentDataType.DOUBLE,Random.nextDouble()))
        } else {
            createSInteractItem(itemStack)
        }
    }

    init {
        SEvent(plugin).register(PlayerInteractEvent::class.java) { e ->
            if (!e.hasItem())return@register
            val item = e.item!!.clone()
            item.amount = 1
            if (!items.containsKey(item))return@register
            if (e.player.inventory.itemInMainHand.isSimilar(item)){
                if (e.hand == EquipmentSlot.OFF_HAND)return@register
            } else {
                if (e.hand == EquipmentSlot.HAND)return@register
            }
            val interactItem = items[item]!!
            if (interactItem.interactCoolDown != 0){
                e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR,*SStr("&c&l使用まで&f:&e&l${ceil(interactItem.interactCoolDown.toDouble() / 2.0) / 10.0}&b&l秒").toBukkitComponent())

                return@register
            }
            interactItem.interactEvents.forEach {
                if (!it.apply(e,interactItem))return@register
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

        SEvent(plugin).register(PlayerItemHeldEvent::class.java) { e ->
            val previousItem = e.player.inventory.getItem(e.previousSlot)
            val newItem = e.player.inventory.getItem(e.newSlot)

            if (previousItem == null && newItem == null)return@register

            if (previousItem != null){
                val item = previousItem.clone()
                item.amount = 1
                if (items.containsKey(item)){
                    val interactItem = items[item]!!
                    interactItem.task.cancel()
                }
            }

            if (newItem != null){
                val item = newItem.clone()
                item.amount = 1
                if (items.containsKey(item)){
                    val interactItem = items[item]!!
                    interactItem.task = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
                        if (interactItem.interactCoolDown <= 0){
                            e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR,*SStr("&a&l使用可能！").toBukkitComponent())
                        } else {
                            e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR,*SStr("&c&l使用まで&f:&e&l${ceil(interactItem.interactCoolDown.toDouble() / 2.0) / 10.0}&b&l秒").toBukkitComponent())
                        }
                    },0,1)
                }
            }

        }
    }
}