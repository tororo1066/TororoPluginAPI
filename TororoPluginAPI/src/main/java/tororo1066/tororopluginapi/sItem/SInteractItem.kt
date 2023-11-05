package tororo1066.tororopluginapi.sItem

import org.bukkit.Material
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.function.BiConsumer
import java.util.function.BiFunction

class SInteractItem(private val manager: SInteractItemManager, private val itemStack: ItemStack) : ItemStack(itemStack) {

    val interactEvents = ArrayList<(PlayerInteractEvent, SInteractItem)->Boolean>()
    val dropEvents = ArrayList<(PlayerDropItemEvent, SInteractItem)->Boolean>()
    val swapEvents = ArrayList<(PlayerSwapHandItemsEvent, SInteractItem)->Boolean>()
    var interactCoolDown = 0
    var initialCoolDown = 0

    var task: BukkitTask? = null

    constructor(manager: SInteractItemManager,material: Material): this(manager, ItemStack(material))

    constructor(manager: SInteractItemManager,sItem: SItem): this(manager, ItemStack(sItem))

    init {
        itemStack.amount = 1
        manager.items[itemStack] = this
    }

    fun setInteractEvent(e: (PlayerInteractEvent,SInteractItem) -> Boolean): SInteractItem {
        interactEvents.add(e)
        manager.items[itemStack] = this
        return this
    }

    fun setDropEvent(e: (PlayerDropItemEvent,SInteractItem) -> Boolean): SInteractItem {
        dropEvents.add(e)
        manager.items[itemStack] = this
        return this
    }

    fun setSwapEvent(e: (PlayerSwapHandItemsEvent,SInteractItem) -> Boolean): SInteractItem {
        swapEvents.add(e)
        manager.items[itemStack] = this
        return this
    }

    fun setInteractCoolDown(coolDown: Int): SInteractItem {
        if (interactCoolDown <= 0){
            interactCoolDown = coolDown
            object : BukkitRunnable() {
                override fun run() {
                    if (interactCoolDown <= 0){
                        interactCoolDown = 0
                        cancel()
                        return
                    }
                    interactCoolDown--
                }
            }.runTaskTimer(manager.plugin,0,1)
            return this
        }
        interactCoolDown = coolDown

        return this
    }

    fun setInitialCoolDown(coolDown: Int): SInteractItem {
        initialCoolDown = coolDown
        return this
    }

    fun delete(){
        task?.cancel()
        manager.items.remove(itemStack)
    }




}