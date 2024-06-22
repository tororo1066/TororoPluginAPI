package tororo1066.nmsutils.v1_20_4

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.SynchedEntityData
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import tororo1066.nmsutils.PacketListener
import kotlin.experimental.or

class PacketListenerImpl: PacketListener {
    override fun injectPlayer(channelName: String, player: Player) {
        val channelDuplexHandler = object : ChannelDuplexHandler() {
            override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
                super.channelRead(ctx, msg)
            }

            override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
                if (msg is ClientboundSetEntityDataPacket) {
                    val p = player.player as? CraftPlayer ?: return
                    if (SEntityImpl.glowData[p.entityId]?.contains(msg.id) == true) {
                        val buf = FriendlyByteBuf(Unpooled.buffer())
                        buf.writeVarInt(msg.id)
                        msg.packedItems.forEach { data ->
                            if (data.id == 0) {
                                SynchedEntityData.DataValue.create(
                                    SEntityImpl.SHARED_FLAGS,
                                    data.value as Byte or 0x40
                                ).write(buf)
                            } else {
                                data.write(buf)
                            }
                        }
                        super.write(ctx, ClientboundSetEntityDataPacket(buf), promise)
                        return
                    }
                }
                super.write(ctx, msg, promise)
            }
        }

        val pipeline = (player as CraftPlayer).handle.connection.connection.channel.pipeline()
        pipeline.addBefore(channelName, "${channelName}_${player.name}", channelDuplexHandler)
    }

    override fun removePlayer(channelName: String, player: Player) {
        val channel = (player as CraftPlayer).handle.connection.connection.channel
        channel.eventLoop().execute {
            channel.pipeline().remove("${channelName}_${player.name}")
        }
    }
}