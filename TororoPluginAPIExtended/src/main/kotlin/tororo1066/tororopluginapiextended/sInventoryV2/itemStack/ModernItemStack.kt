package tororo1066.tororopluginapiextended.sInventoryV2.itemStack

import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.TooltipDisplay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import tororo1066.tororopluginapi.sItem.SItem
import tororo1066.tororopluginapiextended.sInventoryV2.context.InventoryClickContext
import tororo1066.tororopluginapiextended.sInventoryV2.utils.UnaryPlusBuilder

@Suppress("UnstableApiUsage")
open class ModernItemStack(itemStack: ItemStack): SItem(itemStack) {

    companion object {
        private val legacyComponentSerializer = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build()

        private val miniMessageSerializer = MiniMessage.miniMessage()

//        private var defaultItalics = true
//
//        fun setDefaultItalics(italics: Boolean) {
//            defaultItalics = italics
//        }
    }

    constructor(
        itemStack: ItemStack,
        scope: ModernItemStack.() -> Unit
    ): this(itemStack) {
        scope(this)
    }

    constructor(
        material: Material,
        scope: ModernItemStack.() -> Unit
    ): this(ItemStack(material), scope)

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

    var hideTooltip: Boolean
        get() = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY)?.hideTooltip() ?: false
        set(value) {
            val hiddenComponents = itemStack.getData(DataComponentTypes.TOOLTIP_DISPLAY)?.hiddenComponents() ?: setOf()
            itemStack.setData(
                DataComponentTypes.TOOLTIP_DISPLAY,
                TooltipDisplay.tooltipDisplay()
                    .hiddenComponents(hiddenComponents)
                    .hideTooltip(value)
            )
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

    fun lore(builder: UnaryPlusBuilder<Component>.() -> Unit) {
        val loreBuilder = UnaryPlusBuilder<Component>()
        loreBuilder.builder()
        lore = loreBuilder.build()
    }

    fun loreText(builder: UnaryPlusBuilder<String>.() -> Unit) {
        val loreBuilder = UnaryPlusBuilder<String>()
        loreBuilder.builder()
        loreText = loreBuilder.build()
    }

    fun loreMiniMessage(builder: UnaryPlusBuilder<String>.() -> Unit) {
        val loreBuilder = UnaryPlusBuilder<String>()
        loreBuilder.builder()
        loreMiniMessage = loreBuilder.build()
    }

    fun onClick(action: InventoryClickContext.() -> Unit) {
        onClick.add(action)
    }

    fun cancelClickEvent() {
        onClick.add { inventoryClickEvent.isCancelled = true }
    }

    internal fun handleOnClick(context: InventoryClickContext) {
        onClick.forEach { it(context) }
    }
}