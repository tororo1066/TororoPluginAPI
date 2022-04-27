package tororo1066.tororopluginapi.nms.base

import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import tororo1066.tororopluginapi.otherUtils.UsefulUtility

interface SNms {

    fun updateInventoryTitle(p: Player, inv: Inventory, title: String)

    fun pickUpItemPacket(pickUpPlayer: Player, item: Item)

    fun glowPlayer(owner: Player, target: Player, duration: Int)

    companion object{
        fun newInstance() : SNms? {
            var sNms : SNms? = null
            UsefulUtility.sTry({
                val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")
                val clazz = Class.forName("tororo1066.tororopluginapi.nms.v${version}")
                sNms = clazz.getConstructor().newInstance() as SNms
            },{
                sNms = null
            })

            return sNms
        }
    }
}