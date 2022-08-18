package tororo1066.nmsutils

import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

interface SNms {
    fun updateInventoryTitle(p: Player, inv: Inventory, title: String)

    fun pickUpItemPacket(pickUpPlayer: Player, item: Item)

    companion object{
        fun newInstance() : SNms? {
            return try {
                val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")
                val clazz = Class.forName("tororo1066.nmsutils.v${version}.SNmsImpl")
                val instance = clazz.getConstructor().newInstance() as SNms
                instance
            } catch (e: Exception){
                e.printStackTrace()
                return null
            }
        }
    }

    fun getNMSClass(){

    }
}