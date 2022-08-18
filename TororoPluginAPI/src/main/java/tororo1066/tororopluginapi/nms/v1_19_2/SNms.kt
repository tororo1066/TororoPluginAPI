package tororo1066.tororopluginapi.nms.v1_19_2

import net.minecraft.network.chat.ChatMessage
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow
import net.minecraft.world.inventory.Containers
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import tororo1066.tororopluginapi.nms.base.ISNms

class SNms : ISNms {

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
        ep.b.a(PacketPlayOutOpenWindow(ep.bU.j, con, ChatMessage(title)))
        ep.a(ep.bU)
    }

    override fun pickUpItemPacket(pickUpPlayer: Player, item: Item) {

    }
}