package tororo1066.tororopluginapi.sItem

import org.bukkit.Material
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class SInteractItem(private val manager: SInteractItemManager, private val itemStack: ItemStack) : ItemStack(itemStack) {

    val interactEvents = ArrayList<(PlayerInteractEvent, SInteractItem)->Boolean>()
    val dropEvents = ArrayList<(PlayerDropItemEvent, SInteractItem)->Unit>()
    val swapEvents = ArrayList<(PlayerSwapHandItemsEvent, SInteractItem)->Unit>()
    var interactCoolDown = 0
    var initialCoolDown = 0

    var equalFunc: (ItemStack, SInteractItem) -> Boolean =  { itemStack, sInteractItem -> itemStack == ItemStack(sInteractItem) }

    var task: BukkitTask? = null

    constructor(manager: SInteractItemManager,material: Material): this(manager, ItemStack(material))

    constructor(manager: SInteractItemManager,sItem: SItem): this(manager, sItem.build())

    init {
        manager.items[itemStack.apply { amount = 1 }] = this
    }

    fun setInteractEvent(e: (PlayerInteractEvent,SInteractItem) -> Boolean): SInteractItem {
        interactEvents.add(e)
        return this
    }

    fun setDropEvent(e: (PlayerDropItemEvent,SInteractItem) -> Unit): SInteractItem {
        dropEvents.add(e)
        return this
    }

    fun setSwapEvent(e: (PlayerSwapHandItemsEvent,SInteractItem) -> Unit): SInteractItem {
        swapEvents.add(e)
        return this
    }

    fun setEqualFunc(func: (ItemStack, SInteractItem) -> Boolean): SInteractItem {
        equalFunc = func
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