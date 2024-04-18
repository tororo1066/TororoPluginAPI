package tororo1066.nmsutils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Keyed
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scoreboard.Scoreboard
import java.util.UUID

interface SPlayer {

    val bukkitPlayer: Player

    fun updateInventoryTitle(inv: Inventory, title: String)

    fun pickUpPlayer(item: Item)

    fun moveRotation(yaw: Float, pitch: Float)

    fun damagePacket()

    fun placeRecipe(recipe: Keyed, isShift: Boolean)

    fun hideEntity(entity: Entity, hiddenPlayerList: Boolean)

    fun showEntity(entity: Entity)

    fun spawnFakeInvisibleArmorStand(location: Location, slot: EquipmentSlot, item: ItemStack): Int

    fun removeFakeInvisibleArmorStand(entityId: Int)

    fun invisibleItems(slots: List<EquipmentSlot>, invisible: Boolean)

    fun setFakeItem(slot: EquipmentSlot, item: ItemStack)

    fun move(x: Double, y: Double, z: Double)

    fun sendObjective(scoreboard: Scoreboard, objectiveName: String, displayName: String)

    fun sendScore(objectiveName: String, vararg scores: Pair<Int,String>)

    fun initGlowTeam(nameTagVisibility: String)

    companion object{
        fun getSPlayer(p: Player): SPlayer {
            return fromPlayer(p) ?:throw UnsupportedOperationException("SPlayer not supported mc_version ${Bukkit.getServer().minecraftVersion}.")
        }

        private fun fromPlayer(p: Player?): SPlayer? {
            p?:return null
            return try {
                val version = Bukkit.getServer().bukkitVersion.split("-")[0].replace(".","_")
                val clazz = Class.forName("tororo1066.nmsutils.v${version}.SPlayerImpl")
                val instance = clazz.getConstructor(Player::class.java).newInstance(p) as SPlayer
                instance
            } catch (e: Exception){
                null
            }
        }

        fun getSPlayer(uuid: UUID): SPlayer? {
            return fromPlayer(Bukkit.getPlayer(uuid))
        }

        fun getSPlayer(name: String): SPlayer? {
            return fromPlayer(Bukkit.getPlayer(name))
        }


        val hiddenEntities = HashMap<UUID,ArrayList<UUID>>()
    }
}