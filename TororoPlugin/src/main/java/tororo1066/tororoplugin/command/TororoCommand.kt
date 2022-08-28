package tororo1066.tororoplugin.command

import com.earth2me.essentials.Essentials
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
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
import tororo1066.tororopluginapi.sCommand.SCommandObject
import tororo1066.tororopluginapi.utils.toPlayer
import java.util.*
import kotlin.math.floor

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
        .addArg(SCommandArg().addAllowString(Enchantment.values().map { it.key.key.lowercase() }.toTypedArray()))
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
        .addArg(SCommandArg().addAllowString(Attribute.values().map { it.name.lowercase() }.toTypedArray()))
        .addArg(SCommandArg().addAllowType(SCommandArgType.DOUBLE).addAlias("level"))
        .addArg(SCommandArg().addAllowString(EquipmentSlot.values().map { it.name.lowercase() }.toTypedArray()))
        .setPlayerExecutor {
            if (it.sender.inventory.itemInMainHand.type.isAir){
                it.sender.sendMessage(TororoPlugin.prefix + "§c手にアイテムを持ってください")
                return@setPlayerExecutor
            }
            val meta = it.sender.inventory.itemInMainHand.itemMeta!!
            val uuid = UUID.randomUUID()
            meta.addAttributeModifier(Attribute.valueOf(it.args[2].uppercase()), AttributeModifier(uuid,uuid.toString(),it.args[3].toDouble(),AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.valueOf(it.args[4].uppercase())))
            it.sender.inventory.itemInMainHand.itemMeta = meta
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }

    @SCommandBody
    val itemFlags = command().addArg(SCommandArg().addAllowString("item")).addArg(SCommandArg().addAllowString("flags"))
        .addArg(SCommandArg().addAllowString(ItemFlag.values().map { it.name.lowercase() }.toTypedArray()))
        .setPlayerExecutor {
            if (it.sender.inventory.itemInMainHand.type.isAir){
                it.sender.sendMessage(TororoPlugin.prefix + "§c手にアイテムを持ってください")
                return@setPlayerExecutor
            }
            val meta = it.sender.inventory.itemInMainHand.itemMeta!!
            meta.addItemFlags(ItemFlag.valueOf(it.args[2].uppercase()))
            it.sender.inventory.itemInMainHand.itemMeta = meta
            it.sender.sendMessage(TororoPlugin.prefix + "§a変更しました")
        }

    @SCommandBody
    val playerInfo = getPInfoCommand()
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!

            it.sender.sendMessage("§e======${p.name}の情報(クリックでコピー)======")
            it.sender.sendCopyableMsg("§7Name: ${p.name}",p.name)
            it.sender.sendCopyableMsg("§7UUID: ${p.uniqueId}",p.uniqueId.toString())
            it.sender.sendCopyableMsg("§7World: ${p.world.name}",p.world.name)
            it.sender.sendCopyableMsg("§7Location: ${floor(p.location.x * 10.0)/10.0} ${floor(p.location.y * 10.0)/10.0} ${floor(p.location.z * 10.0)/10.0}","${floor(p.location.x * 10.0)/10.0} ${floor(p.location.y * 10.0)/10.0} ${floor(p.location.z * 10.0)/10.0}")
            it.sender.sendCopyableMsg("§7GameMode: ${p.gameMode.name.lowercase()}",p.gameMode.name.lowercase())
            it.sender.sendCopyableMsg("§7Ping: ${if (p.ping < 100) "§a" else if (p.ping < 300) "§6" else "§c"}${p.ping}ms",p.ping.toString())
            it.sender.sendCopyableMsg("§7Locale: ${p.locale().language}",p.locale().language)
            it.sender.sendCopyableMsg("§7Health: ${p.health}/${p.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue}",p.health.toString())
            it.sender.sendCopyableMsg("§7Food: ${p.exhaustion}/20.0",p.exhaustion.toString())
            it.sender.sendCopyableMsg("§7WalkSpeed: ${p.walkSpeed}(Default: 0.2)",p.walkSpeed.toString())
            if (TororoPlugin.essentials != null){
                val isAfk = TororoPlugin.essentials!!.getUser(p).isAfk
                it.sender.sendCopyableMsg("§7Afk: $isAfk",isAfk.toString())
            }
            it.sender.sendMessage("§e======${p.name}の情報(クリックでコピー)======")

        }

    @SCommandBody
    val playerNameInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("name"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7Name: ${p.name}",p.name)
        }

    @SCommandBody
    val playerUUIDInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("uuid"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7UUID: ${p.uniqueId}",p.uniqueId.toString())
        }

    @SCommandBody
    val playerWorldInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("world"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7World: ${p.world.name}",p.world.name)
        }

    @SCommandBody
    val playerLocInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("location"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7Location: ${floor(p.location.x * 10.0)/10.0} ${floor(p.location.y * 10.0)/10.0} ${floor(p.location.z * 10.0)/10.0}","${floor(p.location.x * 10.0)/10.0} ${floor(p.location.y * 10.0)/10.0} ${floor(p.location.z * 10.0)/10.0}")
        }

    @SCommandBody
    val playerGameModeInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("gameMode"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7GameMode: ${p.gameMode.name.lowercase()}",p.gameMode.name.lowercase())
        }

    @SCommandBody
    val playerPingInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("ping"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7Ping: ${if (p.ping < 100) "§a" else if (p.ping < 300) "§6" else "§c"}${p.ping}ms",p.ping.toString())
        }

    @SCommandBody
    val playerLocaleInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("locale"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7Locale: ${p.locale().language}",p.locale().language)
        }

    @SCommandBody
    val playerHealthInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("health"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7Health: ${p.health}/${p.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.baseValue}",p.health.toString())
        }

    @SCommandBody
    val playerFoodInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("food"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7Food: ${p.exhaustion}/20.0",p.exhaustion.toString())
        }

    @SCommandBody
    val playerWalkSpeedInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("walkSpeed"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            it.sender.sendCopyableMsg("§7WalkSpeed: ${p.walkSpeed}(Default: 0.2)",p.walkSpeed.toString())
        }

    @SCommandBody
    val playerAfkInfo = getPInfoCommand().addArg(SCommandArg().addAllowString("afk"))
        .setNormalExecutor {
            val p = it.args[1].toPlayer()!!
            if (TororoPlugin.essentials == null){
                it.sender.sendMessage(TororoPlugin.prefix + "§cこのサーバーにはEssentialsが入っていません")
                return@setNormalExecutor
            }
            val isAfk = TororoPlugin.essentials!!.getUser(p).isAfk
            it.sender.sendCopyableMsg("§7Afk: $isAfk",isAfk.toString())
        }

    @SEvent
    fun onCommandProcess(e: PlayerCommandPreprocessEvent){
        if (e.isCancelled)return
        TororoPlugin.commandLogPlayers.forEach {
            it.toPlayer()?.sendMessage(SStr("&b[Command] &e${e.player.name}-> &b${e.message}").toString())
        }
    }

    private fun getPlayerCommand(): SCommandObject {
        return command().addArg(SCommandArg().addAllowString("player")).addArg(SCommandArg().addAllowType(SCommandArgType.ONLINE_PLAYER))
    }

    private fun getPInfoCommand(): SCommandObject {
        return getPlayerCommand().addArg(SCommandArg().addAllowString("info"))
    }

    private fun CommandSender.sendCopyableMsg(msg: String, copy: String){
        this.sendMessage(Component.text(msg).clickEvent(ClickEvent.copyToClipboard(copy)))
    }

}