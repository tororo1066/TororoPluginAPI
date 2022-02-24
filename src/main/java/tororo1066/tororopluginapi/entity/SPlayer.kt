package tororo1066.tororopluginapi.entity

import net.minecraft.world.entity.EnumMoveType
import net.minecraft.world.phys.Vec3D
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.event.Listener
import tororo1066.tororopluginapi.sInventory.SInventory


class SPlayer(craftPlayer: CraftPlayer) : CraftPlayer((Bukkit.getServer() as CraftServer),craftPlayer.handle) {

    fun openInventory(inventory : SInventory){
        inventory.open(this)
    }

    fun smoothTeleport(to : Location){
        this.handle.move(EnumMoveType.a, Vec3D(to.x,to.y,to.z+10.0))
    }




}


