package tororo1066.nmsutils

import net.minecraft.network.chat.ChatMessageContent
import net.minecraft.network.chat.IChatBaseComponent
import net.minecraft.network.chat.IChatMutableComponent
import net.minecraft.network.protocol.game.PacketPlayOutCollect
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow
import net.minecraft.world.inventory.Containers
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory

class SNmsImpl: SNms {

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
        val packet = PacketPlayOutOpenWindow(ep.bU.j, con, ChatMessageContent(title).c())
        ep.b.a(packet)
        ep.a(ep.bU)
    }

    override fun pickUpItemPacket(pickUpPlayer: Player, item: Item) {
        val packet = PacketPlayOutCollect(item.entityId,pickUpPlayer.entityId,item.itemStack.amount)
        Bukkit.getOnlinePlayers().forEach {
            (it as CraftPlayer).handle.b.a(packet)
        }
    }
}