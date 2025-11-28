package tororo1066.nmsutils.v1_21_8

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.scores.PlayerTeam
import net.minecraft.world.scores.Scoreboard
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import tororo1066.nmsutils.SEntity
import tororo1066.nmsutils.items.GlowColor
import kotlin.experimental.or

class SEntityImpl(entity: Entity): SEntity, CraftEntity((entity as CraftEntity).handle.level().craftServer, entity.handle) {

    override val bukkitEntity: Entity
        get() = this

    companion object {
        val SHARED_FLAGS = EntityDataAccessor(0, EntityDataSerializers.BYTE)
        val glowData = HashMap<Int, HashMap<Int, GlowColor>>() //key: receiver entityId, value: Map of entityId to GlowColor
    }

    override fun sendGlow(glow: Boolean, receivers: Collection<Player>, glowColor: GlowColor) {
        val previous: Byte = handle.entityData.get(SHARED_FLAGS)
        val byte = if (glow) previous or 0x40 else previous
        val glowPacket = ClientboundSetEntityDataPacket(entityId, listOf(SynchedEntityData.DataValue.create(SHARED_FLAGS, byte)))

        val teamPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(
            PlayerTeam(Scoreboard(), glowColor.getTeamName()),
            if (this.entity is net.minecraft.world.entity.player.Player) bukkitEntity.name else uniqueId.toString(),
            if (glow) ClientboundSetPlayerTeamPacket.Action.ADD else ClientboundSetPlayerTeamPacket.Action.REMOVE
        )

        receivers.forEach {
            (it as CraftPlayer).handle.connection.run {
                if (!glow) {
                    val currentGlowData = glowData[it.entityId]?.get(entityId)
                    if (currentGlowData != null && currentGlowData == glowColor) {
                        send(teamPacket) // Only send REMOVE if the glow color matches
                    }
                } else {
                    send(teamPacket)
                }
                send(glowPacket)
                if (glow) {
                    glowData.computeIfAbsent(it.entityId) { HashMap() }[entityId] = glowColor
                } else {
                    glowData[it.entityId]?.remove(entityId)
                }
            }
        }
    }

    override fun setTeam(teamName: String, receivers: Collection<Player>, remove: Boolean) {
        val teamPacket = ClientboundSetPlayerTeamPacket.createPlayerPacket(
            PlayerTeam(Scoreboard(), teamName),
            if (this.entity is net.minecraft.world.entity.player.Player) bukkitEntity.name else uniqueId.toString(),
            if (remove) ClientboundSetPlayerTeamPacket.Action.REMOVE else ClientboundSetPlayerTeamPacket.Action.ADD
        )

        receivers.forEach {
            (it as CraftPlayer).handle.connection.send(teamPacket)
        }
    }
}