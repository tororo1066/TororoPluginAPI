package tororo1066.tororopluginapi.utils

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
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

fun CommandSender.sendMessage(sStr: SStr){
    sStr.sendMessage(this)
}

fun World.broadcast(sStr: SStr){
    this.players.forEach {
        it.sendMessage(sStr)
    }
}

fun World.broadcast(str: String){
    this.players.forEach {
        it.sendMessage(str)
    }
}

fun Player.returnItem(itemStack: ItemStack){
    val addItem = this.inventory.addItem(itemStack)
    if (addItem.isNotEmpty()){
        addItem.forEach { item ->
            this.world.dropItem(this.location, item.value) {
                it.pickupDelay = 0
                it.owner = this.uniqueId
                it.setCanMobPickup(false)
            }
        }
    }
}

fun Player.getAllItem(): Array<ItemStack> {
    val list = mutableListOf<ItemStack>()
    this.inventory.contents.forEach {
        if (it != null && it.type != Material.AIR){
            list.add(it)
        }
    }
    this.inventory.armorContents.forEach {
        if (it.type != Material.AIR){
            list.add(it)
        }
    }
    this.itemOnCursor.let {
        if (it.type != Material.AIR){
            list.add(it)
        }
    }
    return list.toTypedArray()
}