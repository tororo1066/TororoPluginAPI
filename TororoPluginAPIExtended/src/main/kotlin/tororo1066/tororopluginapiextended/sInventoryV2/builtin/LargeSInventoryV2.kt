package tororo1066.tororopluginapiextended.sInventoryV2.builtin

import net.kyori.adventure.text.Component
import org.bukkit.Material
import tororo1066.tororopluginapiextended.sInventoryV2.SInventoryV2
import tororo1066.tororopluginapiextended.sInventoryV2.itemStack.ModernItemStack

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

    abstract val resource: () -> List<ModernItemStack>

    constructor(title: String): this(Component.text(title))

    override fun renderContents() {
        val resourceItems = resource()

        set(45..53, barItem)

        val hasPreviousPage = currentPage > 0
        val hasNextPage = resourceItems.size > (currentPage + 1) * 45

        if (hasPreviousPage) {
            set(45, previousBarItem)
        }

        if (hasNextPage) {
            set(53, nextBarItem)
        }

        resourceItems.drop(currentPage * 45).take(45).forEachIndexed { index, item ->
            set(index, item)
        }
    }
}