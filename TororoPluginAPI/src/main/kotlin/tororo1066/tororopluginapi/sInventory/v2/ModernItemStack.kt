package tororo1066.tororopluginapi.sInventory.v2

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import tororo1066.tororopluginapi.sItem.SItem

open class ModernItemStack(itemStack: ItemStack): SItem(itemStack) {

    companion object {
        private val legacyComponentSerializer = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build()

        private val miniMessageSerializer = MiniMessage.miniMessage()
    }

    private inline fun <T> ifHasMeta(action: (meta: ItemMeta) -> T): T? {
        val meta = itemStack.itemMeta ?: return null
        return action(meta)
    }

    private val onClick: MutableList<InventoryClickContext.() -> Unit> = mutableListOf()

    var displayName: Component?
        get() = ifHasMeta { it.displayName() }
        set(value) {
            itemStack.editMeta { it.displayName(value) }
        }

    var displayNameText : String?
        get() = displayName?.let { legacyComponentSerializer.serialize(it) }
        set(value) {
            displayName = value?.let { legacyComponentSerializer.deserialize(it) }
        }

    var displayNameMiniMessage : String?
        get() = displayName?.let { miniMessageSerializer.serialize(it) }
        set(value) {
            displayName = value?.let { miniMessageSerializer.deserialize(it) }
        }

    var lore: List<Component>?
        get() = ifHasMeta { it.lore() }
        set(value) {
            itemStack.editMeta { it.lore(value) }
        }

    var loreText: List<String>?
        get() = lore?.map { legacyComponentSerializer.serialize(it) }
        set(value) {
            lore = value?.map { legacyComponentSerializer.deserialize(it) }
        }

    var loreMiniMessage: List<String>?
        get() = lore?.map { miniMessageSerializer.serialize(it) }
        set(value) {
            lore = value?.map { miniMessageSerializer.deserialize(it) }
        }

    inline fun displayName(builder: () -> Component) {
        displayName = builder()
    }

    inline fun displayNameText(builder: () -> String) {
        displayNameText = builder()
    }

    inline fun displayNameMiniMessage(builder: () -> String) {
        displayNameMiniMessage = builder()
    }

    inline fun lore(builder: LoreBuilders.LoreBuilder<Component>.() -> Unit) {
        val loreBuilder = LoreBuilders.Component()
        loreBuilder.builder()
        lore = loreBuilder.build()
    }

    inline fun loreText(builder: LoreBuilders.LoreBuilder<String>.() -> Unit) {
        val loreBuilder = LoreBuilders.Text()
        loreBuilder.builder()
        lore = loreBuilder.build()
    }

    inline fun loreMiniMessage(builder: LoreBuilders.LoreBuilder<String>.() -> Unit) {
        val loreBuilder = LoreBuilders.MiniMessage()
        loreBuilder.builder()
        lore = loreBuilder.build()
    }

    fun onClick(action: InventoryClickContext.() -> Unit) {
        onClick.add(action)
    }

    fun cancelClickEvent() {
        onClick.add { inventoryClickEvent.isCancelled = true }
    }


//    fun onClick(action: (e: InventoryClickEvent, modernItem: ModernItemStack) -> Unit) {
//        onClick.add(action)
//    }
//
//    fun cancelClickEvent() {
//        onClick.add { e, _ -> e.isCancelled = true }
//    }
//
//
//    internal fun callOnClick(event: InventoryClickEvent) {
//        onClick.forEach { it(event, this) }
//    }


    sealed class LoreBuilders {
        abstract class LoreBuilder<T> {
            protected val loreList = mutableListOf<net.kyori.adventure.text.Component>()

            abstract fun addLine(line: T)

            operator fun T.unaryPlus() {
                addLine(this)
            }

            fun build(): List<net.kyori.adventure.text.Component> = loreList
        }

        class Component: LoreBuilder<net.kyori.adventure.text.Component>() {
            override fun addLine(line: net.kyori.adventure.text.Component) {
                loreList.add(line)
            }
        }

        class Text: LoreBuilder<String>() {
            override fun addLine(line: String) {
                loreList.add(legacyComponentSerializer.deserialize(line))
            }
        }

        class MiniMessage: LoreBuilder<String>() {
            override fun addLine(line: String) {
                loreList.add(miniMessageSerializer.deserialize(line))
            }
        }
    }
}