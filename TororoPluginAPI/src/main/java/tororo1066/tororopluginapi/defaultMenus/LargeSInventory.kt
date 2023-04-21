package tororo1066.tororopluginapi.defaultMenus

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem


open class LargeSInventory(plugin: JavaPlugin, title: String) : SInventory(plugin,title,6) {

    var items = ArrayList<SInventoryItem>()
    var nowPage = 0

    constructor(title: String): this(SJavaPlugin.plugin,title)

    fun setResourceItems(items: ArrayList<SInventoryItem>) {
        this.items = items
    }

    fun renderBar(){
        val slots = 45..53

        val backGround = SItem(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").toSInventoryItem().setCanClick(false)
        setItems(slots.toList(),backGround)

        val left = SItem(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§l前へ").toSInventoryItem().setCanClick(false).setClickEvent {
            nowPage--
            renderInventory(nowPage)
        }

        val right = SItem(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a§l次へ").toSInventoryItem().setCanClick(false).setClickEvent {
            nowPage++
            renderInventory(nowPage)
        }

        if (nowPage != 0) setItem(slots.first,left)
        if ((nowPage + 1) * 45 <= items.size - 1) setItem(slots.last,right)


    }

    fun renderInventory(page: Int) {
        clear()
        renderBar()
        val startingIndex = page * 45
        var ending = this.items.size - startingIndex
        if (ending > 45) ending = 45
        for (i in 0 until ending) {
            setItem(i, this.items[startingIndex + i])
        }
    }


    override fun afterRenderMenu(p: Player) {
        renderInventory(nowPage)
    }
}