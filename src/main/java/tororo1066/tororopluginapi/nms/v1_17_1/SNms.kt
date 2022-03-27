package tororo1066.tororopluginapi.nms.v1_17_1

import net.minecraft.network.chat.ChatMessage
import net.minecraft.network.protocol.game.PacketPlayOutCollect
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow
import net.minecraft.world.inventory.Containers
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class SNms : tororo1066.tororopluginapi.nms.base.SNms {

    override fun updateInventoryTitle(p: Player, inv: Inventory, title: String) {
        val ep = (p as CraftPlayer).handle
        val con = when(inv.size){
            9-> Containers.a
            18-> Containers.b
            27-> Containers.c
            36-> Containers.d
            45-> Containers.e
            54-> Containers.f
            else-> Containers.f
        }
        val packet = PacketPlayOutOpenWindow(ep.bV.j, con, ChatMessage(title))
        ep.b.sendPacket(packet)
        ep.initMenu(ep.bV)
    }

    override fun pickUpItemPacket(pickUpPlayer: Player, item: Item) {
        val packet = PacketPlayOutCollect(item.entityId,pickUpPlayer.entityId,item.itemStack.amount)
        Bukkit.getOnlinePlayers().forEach {
            (it as CraftPlayer).handle.b.sendPacket(packet)
        }
    }


}