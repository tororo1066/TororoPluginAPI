package tororo1066.tororopluginapiextended.sInventoryV2

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapi.sInventory.v2.context.InventoryClickContext
import tororo1066.tororopluginapi.sInventory.v2.context.InventoryCloseContext
import java.util.concurrent.ConcurrentHashMap

abstract class SInventoryV2(val title: Component, val row: Int): InventoryHolder {

    constructor(
        title: String,
        row: Int,
    ): this(Component.text(title), row)

    companion object {
        val sEvent = SEvent()

        init {
            sEvent.register<InventoryClickEvent> { e ->
                val holder = e.inventory.holder as? SInventoryV2 ?: return@register
                val modernItemStack = holder.items[e.slot] ?: return@register
                val context = InventoryClickContext(e, modernItemStack)
                modernItemStack.handleOnClick(context)
            }

            sEvent.register<InventoryCloseEvent> { e ->
                val holder = e.inventory.holder as? SInventoryV2 ?: return@register
                val context = InventoryCloseContext(e)
                holder.handleOnClose(context)
            }
        }
    }

    val items = ConcurrentHashMap<Int, ModernItemStack>()

    val onClose: MutableList<InventoryCloseContext.() -> Unit> = mutableListOf()

    private var inventory: Inventory = Bukkit.createInventory(this, row, title)

    abstract fun render()

    fun open(player: Player) {
        val inventory = Bukkit.createInventory(this, row, title)
        this.inventory = inventory
        render()
        items.forEach { (slot, item) ->
            inventory.setItem(slot, item.build())
        }

        player.openInventory(inventory)
    }

    override fun getInventory(): Inventory {
        return inventory
    }

//    fun set(index: Int, material: Material, scope: ModernItemStack.() -> Unit) {
//        val item = ModernItemStack(ItemStack(material))
//        item.scope()
//        items[index] = item
//    }

    fun set(index: Int, itemStack: ItemStack, scope: ModernItemStack.() -> Unit) {
        val item = ModernItemStack(itemStack).apply(scope)
        items[index] = item
    }

    fun set(index: Int, material: Material, scope: ModernItemStack.() -> Unit) =
        set(index, ItemStack(material), scope)


    internal fun handleOnClose(context: InventoryCloseContext) {
        onClose.forEach { it(context) }
    }
}

class TestSInventoryV2: SInventoryV2("TestSInventoryV2", 1) {
//    init {
//        set(0, Material.DIAMOND) {
//            displayNameText = "§bTest Item"
//            loreText {
//                +"This is a test item"
//                +"Created using SInventoryV2"
//            }
//            customClickAction()
//        }
//    }

    override fun render() {
        set(0, Material.DIAMOND) {
            displayNameText = "§bTest Item"
            loreText {
                +"This is a test item"
                +"Created using SInventoryV2"
            }
            customClickAction()
        }
    }

    fun ModernItemStack.customClickAction() {
        cancelClickEvent()
        onClick {
            player.sendMessage("You clicked on $displayNameText")
        }
    }
}