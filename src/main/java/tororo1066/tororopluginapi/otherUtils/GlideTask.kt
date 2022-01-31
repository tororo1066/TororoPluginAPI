package tororo1066.tororopluginapi.otherUtils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.TororoPluginAPI

class GlideTask(private val entity: Entity, private val travelLoc: Location, private val total: Int) : Runnable {
    var id = 0
    private var cur = 0
    override fun run() {
        if (cur >= total) {
            Bukkit.getScheduler().cancelTask(id)
        }
        Bukkit.getScheduler().runTask(TororoPluginAPI.plugin, Runnable {
            val loc: Location = entity.location.clone()
            loc.pitch = loc.pitch + travelLoc.pitch
            loc.yaw = loc.yaw + travelLoc.yaw
            if (loc.add(travelLoc.x,travelLoc.y,travelLoc.z).block.type != Material.AIR){
                Bukkit.getScheduler().cancelTask(id)
                return@Runnable
            }
            entity.teleport(loc.add(travelLoc.x, travelLoc.y, travelLoc.z))
            cur++
        })
    }

    companion object{
        fun glide(player: Player, toGlide: Location, ticks: Int) {
            val location = Location(null, 0.0, 0.0, 0.0)
            location.x = (toGlide.x - player.location.x) / ticks
            location.y = (toGlide.y - player.location.y) / ticks
            location.z = (toGlide.z - player.location.z) / ticks
            location.yaw = (toGlide.yaw - player.location.yaw) / ticks
            location.pitch = (toGlide.pitch - player.location.pitch) / ticks
            val task = GlideTask(player, location, ticks)
            task.id = Bukkit.getServer().scheduler.runTaskTimerAsynchronously(TororoPluginAPI.plugin, task, 0, 1).taskId
        }
    }

}