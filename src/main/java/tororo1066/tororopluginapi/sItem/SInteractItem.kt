package tororo1066.tororopluginapi.sItem

import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.sEvent.SEvent
import java.util.function.Consumer

class SInteractItem(plugin: JavaPlugin, private val sItem: SItem) : SItem(sItem) {

    private val events = ArrayList<Consumer<PlayerInteractEvent>>()

    constructor(plugin: JavaPlugin,material: Material): this(plugin,SItem(material))

    constructor(plugin: JavaPlugin,itemStack: ItemStack): this(plugin, SItem(itemStack))

    init {
        SEvent(plugin).register(PlayerInteractEvent::class.java) { e ->
            if (!e.hasItem())return@register
            val item = e.item!!
            if (item != sItem)return@register
            events.forEach {
                it.accept(e)
            }
        }
    }

    fun setInteractEvent(e: Consumer<PlayerInteractEvent>){
        events.add(e)
    }




}