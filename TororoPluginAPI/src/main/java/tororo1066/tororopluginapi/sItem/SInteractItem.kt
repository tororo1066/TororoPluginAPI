package tororo1066.tororopluginapi.sItem

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import java.util.function.BiConsumer
import java.util.function.BiFunction

class SInteractItem(private val manager: SInteractItemManager, private val itemStack: ItemStack) : ItemStack(itemStack) {

    val interactEvents = ArrayList<BiFunction<PlayerInteractEvent,SInteractItem,Boolean>>()
    val dropEvents = ArrayList<BiConsumer<PlayerDropItemEvent,SInteractItem>>()
    var interactCoolDown = 0
    var initialCoolDown = 0

    lateinit var task: BukkitTask

    constructor(manager: SInteractItemManager,material: Material): this(manager, ItemStack(material))

    constructor(manager: SInteractItemManager,sItem: SItem): this(manager, ItemStack(sItem))

    init {
        itemStack.amount = 1
        manager.items[itemStack] = this
    }

    fun setInteractEvent(e: BiFunction<PlayerInteractEvent,SInteractItem,Boolean>): SInteractItem {
        interactEvents.add(e)
        manager.items[itemStack] = this
        return this
    }

    fun setDropEvent(e: BiConsumer<PlayerDropItemEvent,SInteractItem>): SInteractItem {
        dropEvents.add(e)
        manager.items[itemStack] = this
        return this
    }

    fun setInteractCoolDown(coolDown: Int): SInteractItem {
        interactCoolDown = coolDown
        return this
    }

    fun setInitialCoolDown(coolDown: Int): SInteractItem {
        initialCoolDown = coolDown
        return this
    }

    fun delete(){
        task.cancel()
        manager.items.remove(itemStack)
    }




}