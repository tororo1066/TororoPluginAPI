package tororo1066.tororopluginapi.sItem

import net.md_5.bungee.api.ChatMessageType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import tororo1066.tororopluginapi.SDebug
import tororo1066.tororopluginapi.SDebug.Companion.sendDebug
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sEvent.SEvent
import kotlin.math.ceil
import kotlin.random.Random

class SInteractItemManager(val plugin: JavaPlugin, disableCoolTimeView: Boolean = false) {

    val items = HashMap<ItemStack,SInteractItem>()
    val sEvent = SEvent(plugin)

    fun createSInteractItem(itemStack: ItemStack, noDump: Boolean = false): SInteractItem {
        return if (noDump){
            SInteractItem(this,SItem(itemStack).setCustomData(plugin,"tororopluginapi", PersistentDataType.DOUBLE,Random.nextDouble()))
        } else {
            SInteractItem(this,itemStack)
        }
    }

    fun createSInteractItem(material: Material, noDump: Boolean = false): SInteractItem {
        return createSInteractItem(ItemStack(material),noDump)
    }

    fun createSInteractItem(sItem: SItem, noDump: Boolean = false): SInteractItem {
        return createSInteractItem(sItem.build(),noDump)
    }

    fun unregister() {
        sEvent.unregisterAll()
        items.values.forEach {
            it.task?.cancel()
        }
        items.clear()
    }

    fun getItem(itemStack: ItemStack): SInteractItem? {
        return items.values.firstOrNull { it.equalFunc(itemStack,it) }
    }

    fun getItem(player: Player, slot: EquipmentSlot): SInteractItem? {
        return getItem(player.inventory.getItem(slot))
    }

    init {
        sEvent.register(PlayerInteractEvent::class.java) { e ->
            if (e.useItemInHand() != Event.Result.DEFAULT)return@register
            if (!e.hasItem())return@register
            if (e.player.inventory.itemInMainHand.isSimilar(e.item!!)){
                if (e.hand != EquipmentSlot.HAND)return@register
            } else {
                if (e.hand != EquipmentSlot.OFF_HAND)return@register
            }
            val item = e.item!!.clone()
            item.amount = 1
            val interactItem = getItem(item)?:return@register
            if (interactItem.interactCoolDown != 0){
                SStr("&c&l使用まで&f:&e&l${ceil(interactItem.interactCoolDown.toDouble() / 2.0) / 10.0}&b&l秒").actionBar(e.player)
                return@register
            }
            interactItem.interactEvents.forEach {
                if (!it.invoke(e,interactItem))return@register
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

        sEvent.register(PlayerDropItemEvent::class.java) { e ->
            val item = e.itemDrop.itemStack.clone()
            item.amount = 1
            val interactItem = getItem(item)?:return@register
            interactItem.dropEvents.forEach {
                it.invoke(e,interactItem)
            }
        }

        sEvent.register(PlayerSwapHandItemsEvent::class.java) { e ->
            val mainHandItem = e.mainHandItem?.clone()?.apply { amount = 1 }
            val offHandItem = e.offHandItem?.clone()?.apply { amount = 1 }

            e.player.sendDebug("SInteractManager", "§7[PlayerSwapHandItemsEvent] mainHandItem: ${mainHandItem?.type} offHandItem: ${offHandItem?.type}")

            if (mainHandItem != null && !mainHandItem.type.isAir){
                val interactItem = getItem(mainHandItem)
                interactItem?.let {
                    e.player.sendDebug("SInteractManager", "§7[PlayerSwapHandItemsEvent] Invoke mainHandItem swapEvents...")
                    interactItem.swapEvents.forEach {
                        it.invoke(e,interactItem)
                    }
                }
            }

            if (offHandItem != null && !offHandItem.type.isAir){
                val interactItem = getItem(offHandItem)
                interactItem?.let {
                    e.player.sendDebug("SInteractManager", "§7[PlayerSwapHandItemsEvent] Invoke offHandItem swapEvents...")
                    interactItem.swapEvents.forEach {
                        it.invoke(e,interactItem)
                    }
                }
            }
        }

        if (!disableCoolTimeView){

            sEvent.register(PlayerItemHeldEvent::class.java) { e ->
                val previousItem = e.player.inventory.getItem(e.previousSlot)
                val newItem = e.player.inventory.getItem(e.newSlot)

                if (previousItem == null && newItem == null)return@register

                if (previousItem != null){
                    val item = previousItem.clone()
                    item.amount = 1
                    val interactItem = getItem(item)
                    interactItem?.let {
                        e.player.sendDebug("SInteractManager", "§7[PlayerItemHeldEvent] Cancel previousItem task...")
                        interactItem.task?.cancel()
                        SStr("").actionBar(e.player)
                    }
                }

                if (newItem != null){
                    val item = newItem.clone()
                    item.amount = 1
                    val interactItem = getItem(item)
                    interactItem?.let {
                        e.player.sendDebug("SInteractManager", "§7[PlayerItemHeldEvent] Invoke newItem task...")
                        interactItem.task = object : BukkitRunnable() {
                            override fun run() {
                                val mainHandItem = e.player.inventory.itemInMainHand.clone()
                                val offHandItem = e.player.inventory.itemInOffHand.clone()
                                if (!item.isSimilar(mainHandItem) && !item.isSimilar(offHandItem)) {
                                    cancel()
                                    return
                                }
                                if (interactItem.interactCoolDown <= 0) {
                                    SStr("&a&l使用可能！").actionBar(e.player)
                                } else {
                                    SStr("&c&l使用まで&f:&e&l${ceil(interactItem.interactCoolDown.toDouble() / 2.0) / 10.0}&b&l秒").actionBar(e.player)
                                }

                            }
                        }.runTaskTimer(plugin,1, 1)
                    }
                }

            }
        }
    }
}