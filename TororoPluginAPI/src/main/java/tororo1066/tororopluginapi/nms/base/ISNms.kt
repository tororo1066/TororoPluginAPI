package tororo1066.tororopluginapi.nms.base

import org.bukkit.Bukkit
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import tororo1066.tororopluginapi.otherUtils.UsefulUtility

interface ISNms {

    fun updateInventoryTitle(p: Player, inv: Inventory, title: String)

    fun pickUpItemPacket(pickUpPlayer: Player, item: Item)

    companion object{
        fun newInstance() : ISNms? {
            var sNms : ISNms? = null
            UsefulUtility.sTry({
                val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")
                val clazz = Class.forName("tororo1066.tororopluginapi.nms.v${version}")
                sNms = clazz.getConstructor().newInstance() as ISNms
            },{
                it.printStackTrace()
                sNms = null
            })

            return sNms
        }
    }
}