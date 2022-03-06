package tororo1066.tororopluginapi.defaultMenus

import org.bukkit.Material
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem

class CategorySInventory(plugin: JavaPlugin, title: String) : SInventory(plugin,title,6) {

    constructor(plugin: JavaPlugin,category: String,title: String) : this(plugin,title){
        setCategoryName(category)
    }

    var items = LinkedHashMap<String,ArrayList<SInventoryItem>>()
    var nowPage = 0
    var nowCategory = ""
    var categoryIndex = 0

    fun setResourceItems(items: LinkedHashMap<String,ArrayList<SInventoryItem>>) {
        this.items = items
    }

    fun renderBar(){
        val slots = 45..53

        val backGround = SItem(Material.BLUE_STAINED_GLASS_PANE).setDisplayName(" ").toSInventoryItem().setCanClick(false)
        setItems(slots.toList(),backGround)

        val left = SItem(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§l前へ").toSInventoryItem().setCanClick(false).setClickEvent {
            nowPage--
            renderInventory(nowCategory,nowPage)
        }

        val right = SItem(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a§l次へ").toSInventoryItem().setCanClick(false).setClickEvent {
            nowPage++
            renderInventory(nowCategory,nowPage)
        }

        if (nowPage != 0) setItem(slots.first,left)
        if ((nowPage + 1) * 45 <= items.size - 1) setItem(slots.last,right)

        if (items.size == 1)return

        setItem(slots.toList()[4],SItem(Material.PAPER).setDisplayName("§b§l${nowCategory}").setItemAmount(nowPage+1).toSInventoryItem().setCanClick(false))

        val categoryLeft = SItem(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§l前のカテゴリへ").toSInventoryItem().setCanClick(false).setClickEvent {
            categoryIndex--
            nowCategory = items.keys.toList()[categoryIndex]
            nowPage = 0
            renderInventory(nowCategory,nowPage)
        }

        val categoryRight = SItem(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a§l次のカテゴリへ").toSInventoryItem().setCanClick(false).setClickEvent {
            categoryIndex++
            nowCategory = items.keys.toList()[categoryIndex]
            nowPage = 0
            renderInventory(nowCategory,nowPage)
        }

        if (categoryIndex != 0) setItem(slots.toList()[3],categoryLeft)
        if (categoryIndex+2 <= items.keys.size) setItem(slots.toList()[5],categoryRight)


    }

    fun renderInventory(category: String, page: Int) {
        clear()
        renderBar()
        if (!items.containsKey(category))return
        val categoryItems = items[category]!!

        val startingIndex = page * 45
        var ending = categoryItems.size - startingIndex
        if (ending > 45) ending = 45
        for (i in 0 until ending) {
            setItem(i, categoryItems[startingIndex + i])
        }
    }


    override fun afterRenderMenu() {
        renderInventory(nowCategory,nowPage)
    }

    fun setCategoryName(category: String){
        nowCategory = category
    }
}