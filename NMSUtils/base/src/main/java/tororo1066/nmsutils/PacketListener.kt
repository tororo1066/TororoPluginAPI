package tororo1066.nmsutils

import org.bukkit.entity.Player

interface PacketListener {

    fun injectPlayer(channelName: String, player: Player)

    fun removePlayer(channelName: String, player: Player)
}