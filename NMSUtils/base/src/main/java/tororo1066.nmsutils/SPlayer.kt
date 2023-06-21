package tororo1066.nmsutils

import org.bukkit.Bukkit
import org.bukkit.Keyed
import org.bukkit.entity.Entity
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.UUID

interface SPlayer: Player {

    fun updateInventoryTitle(inv: Inventory, title: String)

    fun pickUpPlayer(item: Item)

    fun moveRotation(yaw: Float, pitch: Float)

    fun damagePacket()

    fun placeRecipe(recipe: Keyed, isShift: Boolean)

    fun hideEntity(entity: Entity, hiddenPlayerList: Boolean)

    fun showEntity(entity: Entity)

    companion object{
        fun getSPlayer(p: Player): SPlayer {
            return fromPlayer(p)?:throw UnsupportedOperationException("SPlayer not supported mc_version ${Bukkit.getServer().minecraftVersion}.")
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