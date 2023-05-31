package tororo1066.tororopluginapi.defaultMenus

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer

open class SingleItemInventory(plugin: JavaPlugin, name: String): StrSInventory(plugin,name, listOf(
    "s s s s c s s s s",
    "s s s c . s s s s",
    "s s s s c s s s s",
    "s s s s e s s s s"), hashMapOf()) {
    open var sideBackground: ItemStack = SItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setDisplayName(" ")
    open var centerBackground : ItemStack = SItem(Material.LIME_STAINED_GLASS_PANE).setDisplayName(" ")
    open var selectItem: ItemStack = SItem(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c選択")
    open var nowItem: ItemStack = SItem(Material.AIR)
    var onConfirm: Consumer<ItemStack>? = null

    override fun afterRenderMenu() {
        itemMap.clear()
        itemMap['s'] = SInventoryItem(sideBackground).setCanClick(false)
        itemMap['c'] = SInventoryItem(centerBackground).setCanClick(false)
        itemMap['e'] = SInventoryItem(selectItem).setCanClick(false).setClickEvent {
            getItem(13)?.let { onConfirm?.accept(it) }
        }
        super.afterRenderMenu()
        setItem(13,nowItem)
    }
}