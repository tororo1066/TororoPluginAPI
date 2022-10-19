package tororo1066.tororopluginapi.utils

import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import tororo1066.tororopluginapi.SStr
import java.util.UUID

fun UUID.toPlayer(): Player? {
    return Bukkit.getPlayer(this)
}

fun String.toPlayer(): Player? {
    return Bukkit.getPlayer(this)
}

fun UUID.toPlayer(onFail: (UUID) -> Unit): Player? {
    val p = this.toPlayer()
    if (p == null){
        onFail.invoke(this)
        return null
    }
    return p
}

fun String.toPlayer(onFail: (String) -> Unit): Player? {
    val p = this.toPlayer()
    if (p == null){
        onFail.invoke(this)
        return null
    }
    return p
}

fun Player.sendMessage(sStr: SStr){
    this.sendMessage(sStr.toTextComponent())
}

fun World.broadcast(sStr: SStr){
    this.players.forEach {
        it.sendMessage(sStr)
    }
}

fun World.broadcast(str: String){
    broadcast(SStr(str))
}