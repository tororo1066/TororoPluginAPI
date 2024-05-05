package tororo1066.nmsutils

import org.bukkit.Bukkit
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import tororo1066.nmsutils.items.GlowColor
import java.util.UUID

interface SEntity {

    val bukkitEntity: Entity

    fun sendGlow(glow: Boolean, receivers: Collection<Player>, glowColor: GlowColor = GlowColor.WHITE)


    companion object {

        fun getSEntity(entity: Entity): SEntity {
            return fromEntity(entity) ?: throw UnsupportedOperationException("SEntity not supported mc_version ${Bukkit.getServer().minecraftVersion}.")
        }

        private fun fromEntity(entity: Entity?): SEntity? {
            entity ?: return null
            return try {
                val version = entity.server.bukkitVersion.split("-")[0].replace(".", "_")
                val clazz = Class.forName("tororo1066.nmsutils.v$version.SEntityImpl")
                val instance = clazz.getConstructor(Entity::class.java).newInstance(entity) as SEntity
                instance
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getSEntity(uuid: UUID): SEntity? {
            return fromEntity(Bukkit.getEntity(uuid))
        }
    }
}