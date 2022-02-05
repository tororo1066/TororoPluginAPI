package tororo1066.tororopluginapi.frombukkit

import defaultMenus.LargeSInventory
import defaultMenus.NormalInventory
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.entity.SPlayer
import java.util.*

class SBukkit {

    companion object{

        /**
         * SPlayerを取得する
         * @param uuid UUID
         */
        fun getSPlayer(uuid: UUID): SPlayer? {
            val player = Bukkit.getPlayer(uuid)?:return null
            return SPlayer((player as CraftPlayer))
        }

        /**
         * SPlayerを取得する
         * @param mcid プレイヤーネーム
         */
        fun getSPlayer(mcid: String): SPlayer? {
            val player = Bukkit.getPlayer(mcid)?:return null
            return SPlayer((player as CraftPlayer))
        }

        /**
         * SPlayerを取得する
         * @param player Player
         */
        fun getSPlayer(player: Player): SPlayer {
            return SPlayer((player as CraftPlayer))
        }

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