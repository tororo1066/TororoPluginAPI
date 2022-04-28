package tororo1066.tororopluginapi.otherPlugin

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.World
import org.bukkit.entity.Player

class SWorldGuardAPI {

    private val container = WorldGuard.getInstance().platform.regionContainer

    fun getRegions(player: Player): ArrayList<ProtectedRegion>{
        val loc = player.location
        val regions = container.get(BukkitAdapter.adapt(loc.world))?.getApplicableRegions(BlockVector3.at(loc.x,loc.y,loc.z))?.regions?:return arrayListOf()
        return ArrayList(regions)
    }

    fun inRegion(player: Player, vararg id: String): Boolean {
        return inRegion(player, id.toMutableList())
    }

    fun inRegion(player: Player, id: MutableList<String>): Boolean {
        val regions = getRegions(player)
        if (regions.isEmpty()) return false
        for (region in regions){
            if (id.contains(region.id)) return true
        }
        return false
    }
}