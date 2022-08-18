package tororo1066.tororoplugin.command

import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import tororo1066.tororoplugin.TororoPlugin
import tororo1066.tororopluginapi.SStr
import tororo1066.tororopluginapi.annotation.SCommandBody
import tororo1066.tororopluginapi.annotation.SEvent
import tororo1066.tororopluginapi.sCommand.SCommand
import tororo1066.tororopluginapi.sCommand.SCommandArg
import tororo1066.tororopluginapi.sCommand.SCommandArgType
import tororo1066.tororopluginapi.utils.toPlayer
import java.util.*

class TororoCommand: SCommand("tororo","","tororo.op") {

    @SCommandBody
    val sendToCommandLog = command().addArg(SCommandArg().addAllowString("commandLog")).addArg(SCommandArg().addAllowType(SCommandArgType.BOOLEAN))
        .setPlayerExecutor {
            if (it.args[1].toBoolean()){
                TororoPlugin.commandLogPlayers.add(it.sender.uniqueId)
            } else {
                TororoPlugin.commandLogPlayers.remove(it.sender.uniqueId)
            }
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }

    @SCommandBody
    val itemLore = command().addArg(SCommandArg().addAllowString("item")).addArg(SCommandArg().addAllowString("lore")).addArg(SCommandArg().addAlias("lore(\\nで改行)"))
        .setPlayerExecutor {
            if (it.sender.inventory.itemInMainHand.type.isAir){
                it.sender.sendMessage(TororoPlugin.prefix + "§c手にアイテムを持ってください")
                return@setPlayerExecutor
            }
            val meta = it.sender.inventory.itemInMainHand.itemMeta!!
            meta.lore = it.args[2].replace("&","§").split("\n")
            it.sender.inventory.itemInMainHand.itemMeta = meta
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }

    @SCommandBody
    val itemName = command().addArg(SCommandArg().addAllowString("item")).addArg(SCommandArg().addAllowString("name")).addArg(SCommandArg().addAlias("name"))
        .setPlayerExecutor {
            if (it.sender.inventory.itemInMainHand.type.isAir){
                it.sender.sendMessage(TororoPlugin.prefix + "§c手にアイテムを持ってください")
                return@setPlayerExecutor
            }
            val meta = it.sender.inventory.itemInMainHand.itemMeta!!
            meta.setDisplayName(it.args[2].replace("&","§"))
            it.sender.inventory.itemInMainHand.itemMeta = meta
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }

    @SCommandBody
    val itemCmd = command().addArg(SCommandArg().addAllowString("item")).addArg(SCommandArg().addAllowString("cmd")).addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("カスタムモデルデータ"))
        .setPlayerExecutor {
            if (it.sender.inventory.itemInMainHand.type.isAir){
                it.sender.sendMessage(TororoPlugin.prefix + "§c手にアイテムを持ってください")
                return@setPlayerExecutor
            }
            val meta = it.sender.inventory.itemInMainHand.itemMeta!!
            meta.setCustomModelData(it.args[2].toInt())
            it.sender.inventory.itemInMainHand.itemMeta = meta
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }

    @SCommandBody
    val itemEnchant = command().addArg(SCommandArg().addAllowString("item")).addArg(SCommandArg().addAllowString("enchant"))
        .addArg(SCommandArg().addAllowString(Enchantment.values().map { it.key.key.toLowerCase() }.toTypedArray()))
        .addArg(SCommandArg().addAllowType(SCommandArgType.INT).addAlias("level"))
        .setPlayerExecutor {
            if (it.sender.inventory.itemInMainHand.type.isAir){
                it.sender.sendMessage(TororoPlugin.prefix + "§c手にアイテムを持ってください")
                return@setPlayerExecutor
            }
            it.sender.inventory.itemInMainHand.addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(it.args[2]))!!,it.args[3].toInt())
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }

    @SCommandBody
    val itemAttribute = command().addArg(SCommandArg().addAllowString("item")).addArg(SCommandArg().addAllowString("attribute"))
        .addArg(SCommandArg().addAllowString(Attribute.values().map { it.name.toLowerCase() }.toTypedArray()))
        .addArg(SCommandArg().addAllowType(SCommandArgType.DOUBLE).addAlias("level"))
        .addArg(SCommandArg().addAllowString(EquipmentSlot.values().map { it.name.toLowerCase() }.toTypedArray()))
        .setPlayerExecutor {
            if (it.sender.inventory.itemInMainHand.type.isAir){
                it.sender.sendMessage(TororoPlugin.prefix + "§c手にアイテムを持ってください")
                return@setPlayerExecutor
            }
            val meta = it.sender.inventory.itemInMainHand.itemMeta!!
            val uuid = UUID.randomUUID()
            meta.addAttributeModifier(Attribute.valueOf(it.args[2].toUpperCase()), AttributeModifier(uuid,uuid.toString(),it.args[3].toDouble(),AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.valueOf(it.args[4].toUpperCase())))
            it.sender.inventory.itemInMainHand.itemMeta = meta
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }

    @SCommandBody
    val itemFlags = command().addArg(SCommandArg().addAllowString("item")).addArg(SCommandArg().addAllowString("flags"))
        .addArg(SCommandArg().addAllowString(ItemFlag.values().map { it.name.toLowerCase() }.toTypedArray()))
        .setPlayerExecutor {
            if (it.sender.inventory.itemInMainHand.type.isAir){
                it.sender.sendMessage(TororoPlugin.prefix + "§c手にアイテムを持ってください")
                return@setPlayerExecutor
            }
            val meta = it.sender.inventory.itemInMainHand.itemMeta!!
            meta.addItemFlags(ItemFlag.valueOf(it.args[2].toUpperCase()))
            it.sender.inventory.itemInMainHand.itemMeta = meta
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }




    @SEvent
    fun onCommandProcess(e: PlayerCommandPreprocessEvent){
        if (e.isCancelled)return
        TororoPlugin.commandLogPlayers.forEach {
            it.toPlayer()?.sendMessage(SStr("&b[Command] &e${e.player.name}-> &b${e.message}").toString())
        }
    }

}