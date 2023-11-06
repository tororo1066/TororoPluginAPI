package tororo1066.tororopluginapi.sItem

import net.md_5.bungee.api.ChatMessageType
import org.bukkit.Material
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.sEvent.SEvent
import kotlin.math.ceil
import kotlin.random.Random

class SInteractItemManager(val plugin: JavaPlugin, disableCoolTimeView: Boolean = false) {

    val items = HashMap<ItemStack,SInteractItem>()
    val sEvent = SEvent(plugin)

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

    fun unregister() {
        sEvent.unregisterAll()
        items.values.forEach {
            it.task?.cancel()
        }
        items.clear()
    }

    private fun getItem(itemStack: ItemStack): SInteractItem? {
        return items.values.firstOrNull { it.equalFunc(itemStack,it) }
    }

    init {
        sEvent.register(PlayerInteractEvent::class.java) { e ->
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
                e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR,*SStr("&c&l使用まで&f:&e&l${ceil(interactItem.interactCoolDown.toDouble() / 2.0) / 10.0}&b&l秒").toBukkitComponent())
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
            val mainHandItem = e.player.inventory.itemInMainHand.clone().apply { amount = 1 }
            val offHandItem = e.player.inventory.itemInOffHand.clone().apply { amount = 1 }

            if (!mainHandItem.type.isAir){
                val interactItem = getItem(mainHandItem)?:return@register
                interactItem.swapEvents.forEach {
                    it.invoke(e,interactItem)
                }
            }

            if (!offHandItem.type.isAir){
                val interactItem = getItem(offHandItem)?:return@register
                interactItem.swapEvents.forEach {
                    it.invoke(e,interactItem)
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
                    val interactItem = getItem(item)?:return@register
                    interactItem.task?.cancel()
                    e.player.spigot().sendMessage(ChatMessageType.ACTION_BAR,*SStr("").toBukkitComponent())
                }

                if (newItem != null){
                    val item = newItem.clone()
                    item.amount = 1
                    val interactItem = getItem(item)?:return@register
                    interactItem.task = object : BukkitRunnable() {
                        override fun run() {
                            val mainHandItem = e.player.inventory.itemInMainHand.clone()
                            val offHandItem = e.player.inventory.itemInOffHand.clone()
                            if (!item.isSimilar(mainHandItem) && !item.isSimilar(offHandItem)) {
                                cancel()
                                return
                            }
                            if (interactItem.interactCoolDown <= 0) {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    *SStr("&a&l使用可能！").toBukkitComponent()
                                )
                            } else {
                                e.player.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    *SStr("&c&l使用まで&f:&e&l${ceil(interactItem.interactCoolDown.toDouble() / 2.0) / 10.0}&b&l秒").toBukkitComponent()
                                )
                            }

                        }
                    }.runTaskTimer(plugin,1, 1)
                }

            }
        }
    }
}