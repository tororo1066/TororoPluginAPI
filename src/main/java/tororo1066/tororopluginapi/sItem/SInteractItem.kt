package tororo1066.tororopluginapi.sItem

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.function.Consumer
import kotlin.random.Random

class SInteractItem(private val manager: SInteractItemManager, private val itemStack: ItemStack) : ItemStack(itemStack) {

    val interactEvents = ArrayList<Consumer<PlayerInteractEvent>>()
    val dropEvents = ArrayList<Consumer<PlayerDropItemEvent>>()
    var interactCoolDown = 0
    var initialCoolDown = 0

    constructor(manager: SInteractItemManager,material: Material): this(manager, ItemStack(material))

    constructor(manager: SInteractItemManager,sItem: SItem): this(manager, ItemStack(sItem))

    init {
        itemStack.amount = 1
        manager.items[itemStack] = this
        Bukkit.broadcastMessage((manager.items.containsKey(itemStack).toString()))
    }

    fun setInteractEvent(e: Consumer<PlayerInteractEvent>): SInteractItem {
        interactEvents.add(e)
        manager.items[itemStack] = this
        return this
    }

    fun setDropEvent(e: Consumer<PlayerDropItemEvent>): SInteractItem {
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
        manager.items.remove(itemStack)
    }




}