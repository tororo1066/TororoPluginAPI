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
import org.jetbrains.annotations.Range
import tororo1066.tororopluginapi.sEvent.SEvent
import tororo1066.tororopluginapiextended.sInventoryV2.context.InventoryClickContext
import tororo1066.tororopluginapiextended.sInventoryV2.context.InventoryCloseContext
import java.util.concurrent.ConcurrentHashMap

abstract class SInventoryV2(val title: Component, val row: @Range(from = 1, to = 6) Int): InventoryHolder {

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
                e.isCancelled = true
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

    private val inventory: Inventory = Bukkit.createInventory(this, row * 9, title)

    abstract fun renderContents()

    fun render() {
        items.clear()
        inventory.clear()

        renderContents()

        items.forEach { (slot, item) ->
            inventory.setItem(slot, item.build())
        }
    }

    fun open(player: Player) {
        render()
        player.openInventory(inventory)
    }

    override fun getInventory(): Inventory {
        return inventory
    }

    fun set(index: Int, itemStack: ItemStack, scope: ModernItemStack.() -> Unit = {}) {
        val item = ModernItemStack(itemStack).apply(scope)
        items[index] = item
    }

    fun set(index: Int, material: Material, scope: ModernItemStack.() -> Unit = {}) =
        set(index, ItemStack(material), scope)

    fun set(range: IntRange, modernItemStack: ModernItemStack, scope: ModernItemStack.() -> Unit = {}) {
        val item = modernItemStack.apply(scope)
        range.forEach { index ->
            items[index] = item
        }
    }

    fun set(index: Int, modernItemStack: ModernItemStack, scope: ModernItemStack.() -> Unit = {}) =
        set(index..index, modernItemStack, scope)


    internal fun handleOnClose(context: InventoryCloseContext) {
        onClose.forEach { it(context) }
    }
}