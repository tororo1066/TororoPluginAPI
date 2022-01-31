package tororo1066.tororopluginapi.sInventory

import org.bukkit.Material
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.sItem.SItem
import java.util.concurrent.Executors
import java.util.function.Consumer

class SInventoryItem(itemStack: ItemStack) : SItem(itemStack) {

    val clickEvent = ArrayList<Consumer<InventoryClickEvent>>()
    val asyncClickEvent = ArrayList<Consumer<InventoryClickEvent>>()
    var canClick = true
    val thread = Executors.newCachedThreadPool()

    constructor(material: Material) : this(ItemStack(material))

    constructor(sItem: SItem) : this(sItem as ItemStack)

    init {
        setClickEvent { if (!canClick) it.isCancelled = true }
    }

    fun setClickEvent(event : Consumer<InventoryClickEvent>): SInventoryItem {
        clickEvent.add(event)
        return this
    }

    fun setAsyncClickEvent(event : Consumer<InventoryClickEvent>): SInventoryItem {
        asyncClickEvent.add(event)
        return this
    }

    @JvmName("setCanClick1")
    fun setCanClick(boolean: Boolean): SInventoryItem {
        canClick = boolean
        return this
    }

    fun active(e : InventoryClickEvent){
        for (event in clickEvent){
            event.accept(e)
        }

        for (event in asyncClickEvent){
            thread.execute { event.accept(e) }
        }
    }

}