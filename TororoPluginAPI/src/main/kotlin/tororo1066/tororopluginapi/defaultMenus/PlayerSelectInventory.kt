package tororo1066.tororopluginapi.defaultMenus

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.SJavaPlugin
import tororo1066.tororopluginapi.sInventory.SInventoryItem
import tororo1066.tororopluginapi.sItem.SItem
import java.util.function.Consumer

open class PlayerSelectInventory(plugin: JavaPlugin, title: String): LargeSInventory(plugin, title) {

    var searchName: String = ""
    var onSelect: Consumer<Player>? = null

    constructor(title: String): this(SJavaPlugin.plugin, title)

    init {
        setOnClick {
            it.isCancelled = true
        }
    }

    override fun renderMenu(): Boolean {
        val items = ArrayList<SInventoryItem>()
        Bukkit.getOnlinePlayers().filter { it.name.contains(searchName) }.forEach {
            items.add(SInventoryItem(Material.PLAYER_HEAD)
                .setDisplayName(it.name)
                .setCanClick(false)
                .setClickEvent { _ ->
                    onSelect?.accept(it)
                })
        }

        setResourceItems(items)
        return true
    }

    override fun afterRenderMenu() {
        super.afterRenderMenu()
        setItem(46, createInputItem(
            SItem(Material.OAK_SIGN).setDisplayName("§b検索")
            .addLore("§d現在の値: §r${searchName}")
            .addLore("§c右クリックで削除"), String::class.java, "/<検索名(/cancelでキャンセル)>") { str, _ ->
            searchName = str
        })
    }
}