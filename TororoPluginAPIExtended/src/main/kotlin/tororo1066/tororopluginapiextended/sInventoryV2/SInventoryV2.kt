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
import tororo1066.tororopluginapiextended.sInventoryV2.itemStack.InputItemStack
import tororo1066.tororopluginapiextended.sInventoryV2.itemStack.ModernItemStack
import java.util.concurrent.ConcurrentHashMap

abstract class SInventoryV2(title: Component, row: @Range(from = 1, to = 6) Int): InventoryHolder {

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

    fun <T: Any> inputItem(modernItemStack: ModernItemStack, type: Class<T>, builder: InputItemStack.InputItemStack<T>.() -> Unit): ModernItemStack {
        return InputItemStack.InputItemStack(this, modernItemStack, type).apply(builder).applyToModernItemStack()
    }

    fun <T: Any> inputItem(itemStack: ItemStack, type: Class<T>, builder: InputItemStack.InputItemStack<T>.() -> Unit) =
        inputItem(ModernItemStack(itemStack), type, builder)

    fun <T: Any> inputItem(material: Material, type: Class<T>, builder: InputItemStack.InputItemStack<T>.() -> Unit) =
        inputItem(ItemStack(material), type, builder)

    fun <T: Any> setInputItem(index: Int, modernItemStack: ModernItemStack, type: Class<T>, builder: InputItemStack.InputItemStack<T>.() -> Unit) {
        set(index, inputItem(modernItemStack, type, builder))
    }

    fun <T: Any> setInputItem(index: Int, itemStack: ItemStack, type: Class<T>, builder: InputItemStack.InputItemStack<T>.() -> Unit) =
        setInputItem(index, ModernItemStack(itemStack), type, builder)

    fun <T: Any> setInputItem(index: Int, material: Material, type: Class<T>, builder: InputItemStack.InputItemStack<T>.() -> Unit) =
        setInputItem(index, ItemStack(material), type, builder)

    fun <T: Any> nullableInputItem(modernItemStack: ModernItemStack, type: Class<T>, builder: InputItemStack.NullableInputItemStack<T>.() -> Unit): ModernItemStack {
        return InputItemStack.NullableInputItemStack(this, modernItemStack, type).apply(builder).applyToModernItemStack()
    }

    fun <T: Any> nullableInputItem(itemStack: ItemStack, type: Class<T>, builder: InputItemStack.NullableInputItemStack<T>.() -> Unit) =
        nullableInputItem(ModernItemStack(itemStack), type, builder)

    fun <T: Any> nullableInputItem(material: Material, type: Class<T>, builder: InputItemStack.NullableInputItemStack<T>.() -> Unit) =
        nullableInputItem(ItemStack(material), type, builder)

    fun <T: Any> setNullableInputItem(index: Int, modernItemStack: ModernItemStack, type: Class<T>, builder: InputItemStack.NullableInputItemStack<T>.() -> Unit) {
        set(index, nullableInputItem(modernItemStack, type, builder))
    }

    fun <T: Any> setNullableInputItem(index: Int, itemStack: ItemStack, type: Class<T>, builder: InputItemStack.NullableInputItemStack<T>.() -> Unit) =
        setNullableInputItem(index, ModernItemStack(itemStack), type, builder)

    fun <T: Any> setNullableInputItem(index: Int, material: Material, type: Class<T>, builder: InputItemStack.NullableInputItemStack<T>.() -> Unit) =
        setNullableInputItem(index, ItemStack(material), type, builder)

    internal fun handleOnClose(context: InventoryCloseContext) {
        onClose.forEach { it(context) }
    }
}