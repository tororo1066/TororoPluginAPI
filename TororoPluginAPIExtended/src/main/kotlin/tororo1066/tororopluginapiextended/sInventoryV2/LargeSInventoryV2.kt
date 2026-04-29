package tororo1066.tororopluginapiextended.sInventoryV2

import net.kyori.adventure.text.Component
import org.bukkit.Material

abstract class LargeSInventoryV2(title: Component): SInventoryV2(title, 6) {
    var currentPage = 0

    open var barItem: ModernItemStack = ModernItemStack(Material.CYAN_STAINED_GLASS_PANE) {
        hideTooltip = true
        cancelClickEvent()
    }

    open var previousBarItem: ModernItemStack = ModernItemStack(Material.RED_STAINED_GLASS_PANE) {
        displayNameMiniMessage = "<red><bold>前へ"
        cancelClickEvent()
        onClick {
            currentPage--
            render()
        }
    }

    open var nextBarItem: ModernItemStack = ModernItemStack(Material.LIME_STAINED_GLASS_PANE) {
        displayNameMiniMessage = "<green><bold>次へ"
        cancelClickEvent()
        onClick {
            currentPage++
            render()
        }
    }

    constructor(title: String): this(Component.text(title))

    abstract fun resource(): List<ModernItemStack>

    override fun render() {
        val resourceItems = resource()

        set(45..53, barItem)

        val hasPreviousPage = currentPage > 0
        val hasNextPage = (currentPage + 1) * 45 <= resourceItems.size

        if (hasPreviousPage) {
            set(45, previousBarItem)
        }

        if (hasNextPage) {
            set(53, nextBarItem)
        }

        val start = currentPage * 45
        var end = resourceItems.size - currentPage
        if (end > 45) end = 45
        for (i in 0 until end) {
            set(i, resourceItems[start + i])
        }
    }
}