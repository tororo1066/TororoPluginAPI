package tororo1066.tororopluginapi.defaultMenus

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventory
import tororo1066.tororopluginapi.sInventory.SInventoryItem

open class StrSInventory(plugin: JavaPlugin, name: String, val itemsString: List<String>, val itemMap: HashMap<Char,SInventoryItem>): SInventory(plugin,name,itemsString.size) {

    constructor(name: String, itemsString: List<String>, buttons: HashMap<Char,SInventoryItem>): this(SJavaPlugin.plugin,name,itemsString,buttons)


    override fun afterRenderMenu(p: Player) {
        itemsString.forEachIndexed { listIndex, str ->
            str.forEachIndexed second@ { i, c ->
                val trueIndex = i + listIndex * 18
                if (trueIndex % 2 == 1)return@second
                if (c == ' ')return@second
                if (itemMap.containsKey(c)){
                    setItem(trueIndex / 2,itemMap[c]!!)
                }
            }
        }
        return
    }

    class Builder(){

        constructor(init: Builder.() -> Unit): this(){
            init()
        }

        private var name = ""
        private var plugin: JavaPlugin? = null
        private var itemsStr = listOf<String>()
        private val buttons = HashMap<Char,SInventoryItem>()

        fun setName(name: String): Builder{
            this.name = name
            return this
        }

        fun setItems(vararg items: String): Builder{
            this.itemsStr = items.toList()
            return this
        }

        fun addButton(char: Char, item: SInventoryItem): Builder{
            buttons[char] = item
            return this
        }

        fun setPlugin(plugin: JavaPlugin): Builder{
            this.plugin = plugin
            return this
        }

        fun build(): StrSInventory{
            if (plugin == null) plugin = SJavaPlugin.plugin
            return StrSInventory(plugin!!,name,itemsStr,buttons)
        }
    }
}