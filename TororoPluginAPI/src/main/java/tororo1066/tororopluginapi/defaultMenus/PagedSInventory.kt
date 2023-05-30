package tororo1066.tororopluginapi.defaultMenus

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem

open class PagedSInventory(plugin: JavaPlugin,name: String,row: Int): SInventory(plugin,name,row) {

    private var resourceList = ArrayList<HashMap<Int,SInventoryItem>>()

    var nowPage = 0

    private var leftSlots = arrayListOf<Int>()
    private var rightSlots = arrayListOf<Int>()

    constructor(name: String,row: Int): this(SJavaPlugin.plugin,name,row)

    fun setResourceItems(items: ArrayList<HashMap<Int,SInventoryItem>>) {
        this.resourceList = items
    }


    fun addPage(inv: SInventory){
        resourceList.add(inv.getSInvItems())
    }

    fun addPage(items: HashMap<Int,SInventoryItem>){
        this.resourceList.add(items)
    }

    fun setLeftSlots(slots: List<Int>){
        leftSlots = ArrayList(slots)
    }

    fun setRightSlots(slots: List<Int>){
        rightSlots = ArrayList(slots)
    }

    fun addLeftSlot(slot: Int){
        leftSlots.add(slot)
    }

    fun addRightSlot(slot: Int){
        rightSlots.add(slot)
    }


    fun renderBar(){

        val left = SItem(Material.RED_STAINED_GLASS_PANE).setDisplayName("§c§l前へ").toSInventoryItem().setCanClick(false).setClickEvent {
            nowPage--
            renderInventory(nowPage)
        }

        val right = SItem(Material.LIME_STAINED_GLASS_PANE).setDisplayName("§a§l次へ").toSInventoryItem().setCanClick(false).setClickEvent {
            nowPage++
            renderInventory(nowPage)
        }

        if (nowPage != 0){
            leftSlots.forEach {
                setItem(it,left)
            }
        }

        if (nowPage+1 < resourceList.size){
            rightSlots.forEach {
                setItem(it,right)
            }
        }

    }

    fun renderInventory(page: Int) {
        clear()

        for (i in 0 until row*9) {
            setItem(i, this.resourceList[page][i]?:continue)
        }
        renderBar()
    }


    override fun afterRenderMenu() {
        renderInventory(0)
    }

}