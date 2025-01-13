package tororo1066.tororopluginapi.otherPlugin

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flag
import com.sk89q.worldguard.protection.flags.FlagContext
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.concurrent.CompletableFuture

/**
 * WorldGuardを楽に使えるクラス
 */
class SWorldGuardAPI {

    private val container = WorldGuard.getInstance().platform.regionContainer
    private val flagRegistry = WorldGuard.getInstance().flagRegistry

    /**
     * プレイヤーの位置のregionをすべて取得する
     * @param player [プレイヤー][Player]
     * @return ArrayListのRegionクラス
     */
    fun getRegions(player: Player): ArrayList<ProtectedRegion> {
        return getRegions(player.location)
    }

    /**
     * Locationのregionをすべて取得する
     * @param loc [Location]
     * @return ArrayListのRegionクラス
     */
    fun getRegions(loc: Location): ArrayList<ProtectedRegion> {
        val regions = container.get(BukkitAdapter.adapt(loc.world))?.getApplicableRegions(BlockVector3.at(loc.x,loc.y,loc.z))?.regions?:return arrayListOf()
        return ArrayList(regions)
    }

    fun getRegion(world: World, id: String): ProtectedRegion? {
        return container.get(BukkitAdapter.adapt(world))?.getRegion(id)
    }

    /**
     * プレイヤーが特定のregionにいるか確認する
     * @param player プレイヤー
     * @param id regionのid
     * @return [Boolean]
     */
    fun inRegion(player: Player, vararg id: String): Boolean {
        return inRegion(player, id.toMutableList())
    }

    /**
     * プレイヤーが特定のregionにいるか確認する
     * @param player プレイヤー
     * @param id regionのid
     * @return [Boolean]
     */
    fun inRegion(player: Player, id: MutableList<String>): Boolean {
        val regions = getRegions(player)
        if (regions.isEmpty()) return false
        for (region in regions){
            if (id.contains(region.id)) return true
        }
        return false
    }

    fun setFlags(region: ProtectedRegion, flags: Map<String, String>) {
        for (flagEntry in flags) {
            val flag = Flags.fuzzyMatchFlag(flagRegistry, flagEntry.key)
            if (flag != null) {
                try {
                    setFlag(region, flag, flagEntry.value)
                } catch (e: InvalidFlagFormat) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun <V> setFlag(region: ProtectedRegion, flag: Flag<V>, value: String) {
        region.setFlag(flag, flag.parseInput(FlagContext.create().setInput(value).setObject("region", region).build()))
    }

    fun createRegion(world: World, id: String, min: Vector, max: Vector): CompletableFuture<ProtectedRegion?> {
        val bv3min = BlockVector3.at(min.x, min.y, min.z)
        val bv3max = BlockVector3.at(max.x, max.y, max.z)
        val region = ProtectedCuboidRegion(id, bv3min, bv3max)
        val regions = container.get(BukkitAdapter.adapt(world)) ?: return CompletableFuture.completedFuture(null)
        regions.addRegion(region)
        return CompletableFuture.supplyAsync {
            regions.saveChanges()
            region
        }
    }
}