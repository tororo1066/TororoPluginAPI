package tororo1066.nmsutils.v1_19_3

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket.RelativeArgument
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket
import net.minecraft.world.inventory.MenuType
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import tororo1066.nmsutils.SNms

class SNmsImpl: SNms {

    override fun updateInventoryTitle(p: Player, inv: Inventory, title: String) {
        val ep = (p as CraftPlayer).handle

        val con = when(inv.size){
            9-> MenuType.GENERIC_9x1
            18-> MenuType.GENERIC_9x2
            27-> MenuType.GENERIC_9x3
            36-> MenuType.GENERIC_9x4
            45-> MenuType.GENERIC_9x5
            54-> MenuType.GENERIC_9x6
            else-> MenuType.GENERIC_9x6
        }
        val packet = ClientboundOpenScreenPacket(ep.containerMenu.containerId, con, Component.empty().append(title))
        ep.connection.send(packet)
        ep.initMenu(ep.containerMenu)
    }

    override fun pickUpItemPacket(pickUpPlayer: Player, item: Item) {
        val packet = ClientboundTakeItemEntityPacket(item.entityId,pickUpPlayer.entityId,item.itemStack.amount)
        Bukkit.getOnlinePlayers().forEach {
            (it as CraftPlayer).handle.connection.send(packet)
        }
    }

    override fun moveRotation(p: Player, yaw: Float, pitch: Float) {
        val packet = ClientboundPlayerPositionPacket(0.0,0.0,0.0,yaw,pitch,RelativeArgument.values().toSet(),0,true)
        (p as CraftPlayer).handle.connection.send(packet)
    }

    override fun damagePacket(p: Player) {
        val packet = ClientboundEntityEventPacket((p as CraftPlayer).handle,2)
        Bukkit.getOnlinePlayers().forEach {
            (it as CraftPlayer).handle.connection.send(packet)
        }
    }

}