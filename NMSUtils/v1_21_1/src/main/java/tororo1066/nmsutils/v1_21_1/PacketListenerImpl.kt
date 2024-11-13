package tororo1066.nmsutils.v1_21_1

import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket
import net.minecraft.network.syncher.SynchedEntityData
import org.bukkit.craftbukkit.entity.CraftPlayer
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
                        val dataList = ArrayList<SynchedEntityData.DataValue<*>>()
                        msg.packedItems.forEach { data ->
                            if (data.id == 0) {
                                dataList.add(SynchedEntityData.DataValue.create(
                                    SEntityImpl.SHARED_FLAGS,
                                    data.value as Byte or 0x40
                                ))
                            } else {
                                dataList.add(data)
                            }
                        }
                        super.write(ctx, ClientboundSetEntityDataPacket(msg.id, dataList), promise)
                        return
                    }
                }
                super.write(ctx, msg, promise)
            }
        }

        val pipeline = (player as CraftPlayer).handle.connection.connection.channel.pipeline()
        pipeline.remove(channelName)
        pipeline.addBefore("packet_handler", channelName, channelDuplexHandler)
    }

    override fun removePlayer(channelName: String, player: Player) {
        val channel = (player as CraftPlayer).handle.connection.connection.channel
        channel.pipeline().remove(channelName)
    }
}