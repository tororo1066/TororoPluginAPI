package tororo1066.tororopluginapi.frombukkit

import tororo1066.tororopluginapi.defaultMenus.NormalInventory
import org.bukkit.plugin.java.JavaPlugin

class SBukkit {

    companion object{

        /**
         * SInventoryを作る
         * @param plugin メインのプラグイン
         * @param name インベントリの名前
         * @param row インベントリの大きさ(1~6)
         */
        fun createSInventory(plugin: JavaPlugin, name: String, row: Int): NormalInventory {
            return NormalInventory(plugin, name, row)
        }

    }
}