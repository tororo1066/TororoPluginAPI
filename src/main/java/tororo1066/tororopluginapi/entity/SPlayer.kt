package tororo1066.tororopluginapi.entity

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_17_R1.CraftServer
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import tororo1066.tororopluginapi.otherUtils.GlideTask
import tororo1066.tororopluginapi.sInventory.SInventory


class SPlayer(craftPlayer: CraftPlayer) : CraftPlayer((Bukkit.getServer() as CraftServer),craftPlayer.handle), Listener {


    fun openInventory(inventory : SInventory){
        inventory.open(this)
    }

    fun smoothTeleport(to : Location,ticks: Int,plugin: JavaPlugin){
        GlideTask.glide(this,to,ticks,plugin)
    }




}


