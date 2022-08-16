package tororo1066.tororopluginapi.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

fun UUID.toPlayer(): Player? {
    return Bukkit.getPlayer(this)
}

fun String.toPlayer(): Player? {
    return Bukkit.getPlayer(this)
}