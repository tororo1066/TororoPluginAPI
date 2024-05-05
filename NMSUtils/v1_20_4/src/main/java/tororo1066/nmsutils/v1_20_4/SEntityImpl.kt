package tororo1066.nmsutils.v1_20_4

import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundBundlePacket
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import tororo1066.nmsutils.SEntity
import tororo1066.nmsutils.items.GlowColor
import kotlin.experimental.or

class SEntityImpl(entity: Entity): SEntity, CraftEntity((entity as CraftEntity).handle.level().craftServer, entity.handle) {

    override val bukkitEntity: Entity
        get() = this

    companion object {
        private val SHARED_FLAGS = EntityDataAccessor(0, EntityDataSerializers.BYTE)
    }

    override fun sendGlow(glow: Boolean, receivers: Collection<Player>, glowColor: GlowColor) {
        val previous: Byte = if (handle.entityData.isEmpty) 0 else handle.entityData.get(SHARED_FLAGS)
        val byte = if (glow) previous or 0x40 else previous
        val glowPacket = ClientboundSetEntityDataPacket(-entityId, listOf(SynchedEntityData.DataValue.create(SHARED_FLAGS, byte)))

        val teamBuf = FriendlyByteBuf(Unpooled.buffer())
        teamBuf.writeUtf(glowColor.getTeamName())
        teamBuf.writeByte(if (glow) 3 else 4)
        teamBuf.writeCollection(listOf(if (bukkitEntity is Player) bukkitEntity.name else uniqueId.toString()), FriendlyByteBuf::writeUtf)
        val teamPacket = ClientboundSetPlayerTeamPacket(teamBuf)
        val bundlePacket = ClientboundBundlePacket(listOf(glowPacket,teamPacket))
        receivers.forEach {
            (it as CraftPlayer).handle.connection.send(bundlePacket)
        }
    }
}