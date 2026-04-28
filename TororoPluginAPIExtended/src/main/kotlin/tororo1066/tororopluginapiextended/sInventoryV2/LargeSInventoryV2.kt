package tororo1066.tororopluginapiextended.sInventoryV2

import net.kyori.adventure.text.Component
import org.bukkit.Material

open class LargeSInventoryV2(title: Component): SInventoryV2(title, 6) {
    var resourceItems: List<ModernItemStack> = listOf()
    var currentPage = 0

    open var barItem: ModernItemStack = ModernItemStack(Material.CYAN_STAINED_GLASS_PANE) {

    }

    open var previousBarItem: ModernItemStack = ModernItemStack(Material.RED_STAINED_GLASS_PANE) {
        displayNameMiniMessage = "<red><bold>前へ"
    }

    open var nextBarItem: ModernItemStack = ModernItemStack(Material.LIME_STAINED_GLASS_PANE) {
        displayNameMiniMessage = "<green><bold>次へ"
    }

    override fun render() {

    }
}